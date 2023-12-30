package com.example.taskermobile.activities.kanbanboard

import SharedPreferencesService
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.eventlisteners.OnItemClickListener
import com.example.taskermobile.utils.getIdFromToken
import com.example.taskermobile.viewadapters.KanbanBoardAdapter
import com.example.taskermobile.viewmodels.KanbanBoardViewModel
import com.example.taskermobile.viewmodels.TokenViewModel
import com.example.taskermobile.viewmodels.UserViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class KanbanBoardDetailFragment : Fragment() {

    private val kanbanBoardViewModel by viewModel<KanbanBoardViewModel>()
    private val tokenViewModel by viewModel<TokenViewModel>()
    private val userViewModel by viewModel<UserViewModel>()

    private val sharedPreferences: SharedPreferencesService by inject()

    private lateinit var boardAdapter: KanbanBoardAdapter
    private lateinit var kanbanBoardRecyclerView: RecyclerView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var createTaskButton: Button
    private lateinit var title: TextView
    private lateinit var editButton: Button
    private lateinit var editTitle: EditText
    private lateinit var manageTaskStatuses: Button
    private lateinit var userAdminProject: List<String>

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
        manageTaskStatuses = view.findViewById(R.id.manageTaskStatuses)
        createTaskButton = view.findViewById(R.id.createTaskButton)
        editTitle = EditText(requireContext())
        kanbanBoardRecyclerView = view.findViewById(R.id.kanbanBoardRecyclerView)
        kanbanBoardRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

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
                        setUpListeners(board)

                        title.text = board.title
                        boardAdapter = KanbanBoardAdapter(board.columns ?: listOf(), object : OnItemClickListener {
                            override fun onItemClick(id: String) {
                                findNavController().navigate(
                                    R.id.action_kanbanBoardDetailFragment_to_taskDetailFragment,
                                    bundleOf("TASK_ID" to id))
                            }
                        })

                        kanbanBoardRecyclerView.adapter = boardAdapter
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
    }

    private fun setUpListeners(board: KanbanBoardModel) {
        if (userAdminProject.contains(board.projectId)) {
            editButton.visibility = View.VISIBLE
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
