package com.example.taskermobile.activities.kanbanboard

import SharedPreferencesService
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.R
import com.example.taskermobile.activities.project.ProjectDetailFragment
import com.example.taskermobile.model.kanbanboard.KanbanBoardCreateModel
import com.example.taskermobile.model.kanbanboard.KanbanBoardModel
import com.example.taskermobile.model.kanbanboard.KanbanBoardUpdateModel
import com.example.taskermobile.model.task.TaskBoardPreviewModel
import com.example.taskermobile.model.task.TaskPreviewModel
import com.example.taskermobile.model.task.TaskUpdateStatusModel
import com.example.taskermobile.model.taskstatus.TaskStatusBoardModel
import com.example.taskermobile.model.taskstatus.TaskStatusModel
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.apiRequestFlow
import com.example.taskermobile.utils.eventlisteners.OnItemClickListener
import com.example.taskermobile.utils.getIdFromToken
import com.example.taskermobile.viewadapters.KanbanBoardAdapter
import com.example.taskermobile.viewmodels.KanbanBoardViewModel
import com.example.taskermobile.viewmodels.TaskViewModel
import com.example.taskermobile.viewmodels.TokenViewModel
import com.example.taskermobile.viewmodels.UserViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class KanbanBoardDetailFragment : Fragment() {

    private val kanbanBoardViewModel by viewModel<KanbanBoardViewModel>()
    private val taskViewModel by viewModel<TaskViewModel>()
    private val tokenViewModel by viewModel<TokenViewModel>()
    private val userViewModel by viewModel<UserViewModel>()

    private val sharedPreferences: SharedPreferencesService by inject()

    private lateinit var kanbanBoardRecyclerView: RecyclerView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var createTaskButton: Button
    private lateinit var title: TextView
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    private lateinit var editTitle: EditText
    private lateinit var manageTaskStatuses: Button
    private lateinit var userAdminProject: List<String>
    private lateinit var projectId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.kanbanboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        title = view.findViewById(R.id.title)
        editButton = view.findViewById(R.id.editBoard)
        deleteButton = view.findViewById(R.id.deleteBoard)
        manageTaskStatuses = view.findViewById(R.id.manageTaskStatuses)
        createTaskButton = view.findViewById(R.id.createTaskButton)
        editTitle = EditText(requireContext())
        kanbanBoardRecyclerView = view.findViewById(R.id.kanbanBoardRecyclerView)
        kanbanBoardRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val boardId = arguments?.getString("KANBAN_BOARD_ID")
            ?: sharedPreferences.retrieveData("lastKanbanBoard")

        if (boardId == null) {
            findNavController().navigate(R.id.action_kanbanBoardDetailFragment_to_projectsPageFragment)
            return
        }

        sharedPreferences.saveData("lastKanbanBoard", boardId)

        setUpObservers(boardId)
    }

    private fun setUpObservers(boardId: String) {
        tokenViewModel.token.observe(viewLifecycleOwner) { tokenValue ->
            if (tokenValue != null) {
                loadingIndicator.visibility = View.GONE

                val userId = getIdFromToken(token = tokenValue.token)

                userViewModel.get(userId!!)
            } else {
                loadingIndicator.visibility = View.VISIBLE
            }
        }

        userViewModel.userGetResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }

                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE

                    userAdminProject = apiResponse.data?.underControlProjects ?: emptyList()

                    kanbanBoardViewModel.getById(boardId)
                }

                is ApiResponse.Failure -> {
                    loadingIndicator.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Something went wrong: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        kanbanBoardViewModel.kanbanBoardResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }
                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    apiResponse.data?.let { board ->
                        projectId = board.projectId!!
                        setUpListeners(board)

                        title.text = board.title

                        kanbanBoardRecyclerView.adapter = KanbanBoardAdapter(board.columns ?: listOf(),
                            object : OnItemClickListener {
                            override fun onItemClick(id: String) {
                                findNavController().navigate(
                                    R.id.action_kanbanBoardDetailFragment_to_taskDetailFragment,
                                    bundleOf("TASK_ID" to id))
                            }
                            override fun onItemLongClick(task: TaskBoardPreviewModel, allStatuses: List<TaskStatusBoardModel>) {
                                val popupMenu = PopupMenu(
                                    requireContext(),
                                    view
                                )

                                allStatuses.forEach { status ->
                                    popupMenu.menu.add(
                                        Menu.NONE,
                                        status.id.hashCode(),
                                        Menu.NONE,
                                        status.name
                                    )
                                }

                                popupMenu.setOnMenuItemClickListener { menuItem ->
                                    val newStatus = allStatuses.find { it.id.hashCode() == menuItem.itemId }
                                    taskViewModel.update(TaskUpdateStatusModel(task.id, newStatus?.id!!))
                                    Toast.makeText(requireContext(),
                                        "Status changed to: ${newStatus.name}",
                                        Toast.LENGTH_LONG).show()
                                    true
                                }
                                popupMenu.show()
                            }
                        })
                    }
                }
                is ApiResponse.Failure -> {
                    loadingIndicator.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Failed to kanban board: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        kanbanBoardViewModel.kanbanUpdateResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }
                is ApiResponse.Success -> {
                    kanbanBoardViewModel.getById(boardId)
                }
                is ApiResponse.Failure -> {
                    loadingIndicator.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Network error: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        kanbanBoardViewModel.kanbanDeleteResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }
                is ApiResponse.Success -> {
                    Toast.makeText(
                        requireContext(),
                        "Board deleted successfully",
                        Toast.LENGTH_LONG
                    ).show()
                    findNavController().navigate(R.id.action_kanbanBoardDetailFragment_to_projectDetailFragment,
                        bundleOf("PROJECT_ID" to projectId))
                }
                is ApiResponse.Failure -> {
                    loadingIndicator.visibility = View.GONE

                    Toast.makeText(
                        requireContext(),
                        "Error: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()

                    kanbanBoardViewModel.getById(boardId)
                }
            }
        }

        taskViewModel.taskUpdateResponse.observe(viewLifecycleOwner) {apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }
                is ApiResponse.Success -> {
                    kanbanBoardViewModel.getById(boardId)
                }
                is ApiResponse.Failure -> {
                    loadingIndicator.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Network error: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun setUpListeners(board: KanbanBoardModel) {
        if (userAdminProject.contains(board.projectId)) {
            editButton.visibility = View.VISIBLE
            deleteButton.visibility = View.VISIBLE
            editTitle.setText(board.title)

            if (editTitle.parent != null) {
                (editTitle.parent as ViewGroup).removeView(editTitle)
            }

            editButton.setOnClickListener {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Update board")
                        .setView(editTitle)
                        .setPositiveButton("Update") { dialog, which ->
                            val name = editTitle.text.toString()
                            if (name.isEmpty()) {
                                Toast.makeText(requireContext(), "The board name cannot be empty",
                                    Toast.LENGTH_SHORT).show()
                            } else if (name == board.title) {
                                Toast.makeText(requireContext(), "The board name is the same as previous",
                                    Toast.LENGTH_SHORT).show()
                            }
                            else {
                                kanbanBoardViewModel.update(KanbanBoardUpdateModel(board.id!!, name))
                            }
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
            }

            deleteButton.setOnClickListener {
                kanbanBoardViewModel.delete(board.id!!)
            }

            manageTaskStatuses.visibility = View.VISIBLE

            manageTaskStatuses.setOnClickListener {
                findNavController().navigate(R.id.action_kanbanBoardDetailFragment_to_taskStatusListFragment,
                    bundleOf("KANBAN_BOARD_ID" to board.id))
            }
        }

        createTaskButton.setOnClickListener {
            findNavController().navigate(R.id.action_kanbanBoardDetailFragment_to_taskCreateFragment)
        }
    }
}
