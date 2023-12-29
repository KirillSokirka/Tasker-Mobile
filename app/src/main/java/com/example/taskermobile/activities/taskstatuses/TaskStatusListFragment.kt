package com.example.taskermobile.activities.taskstatuses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.R
import com.example.taskermobile.model.taskstatus.TaskStatusCreateModel
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.eventlisteners.OnItemClickListener
import com.example.taskermobile.viewadapters.TaskStatusAdapter
import com.example.taskermobile.viewmodels.KanbanBoardViewModel
import com.example.taskermobile.viewmodels.TaskStatusViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TaskStatusListFragment: Fragment() {
    private val kanbanBoardViewModel by viewModel<KanbanBoardViewModel>()
    private val taskStatusViewModel by viewModel<TaskStatusViewModel>()
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var createStatus: Button
    private lateinit var statusName: EditText
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tasks_statuses_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val boardId = arguments?.getString("KANBAN_BOARD_ID")

        if (boardId == null) {
            findNavController().navigate(R.id.action_kanbanBoardDetailFragment_to_projectsPageFragment)
            return
        }

        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        recyclerView = view.findViewById(R.id.tasksStatusRecyclerView)
        createStatus = view.findViewById(R.id.buttonCreate)
        statusName = EditText(requireContext())

        kanbanBoardViewModel.getById(boardId)
        setUpObservers(boardId)
    }

    private fun setUpObservers(boardId: String) {
        kanbanBoardViewModel.kanbanBoardResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }

                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    apiResponse.data?.let { board ->
                        loadingIndicator.visibility = View.GONE
                        recyclerView.layoutManager = LinearLayoutManager(context)
                        recyclerView.adapter = TaskStatusAdapter(apiResponse.data.columns
                            ?: emptyList(), object : OnItemClickListener {
                            override fun onItemClick(id: String) {
                                findNavController().navigate(
                                    R.id.action_projectsPageFragment_to_projectDetailFragment,
                                    bundleOf("PROJECT_ID" to id))
                            }
                        })

                        addEventListener(apiResponse.data.columns?.map { it.name } ?: emptyList(), board.id)

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

        taskStatusViewModel.taskStatusCreateResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }

                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "New task status was created",
                        Toast.LENGTH_LONG
                    ).show()
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

    private fun addEventListener(map: List<String?>, id: String?) {


        createStatus.setOnClickListener {
            if (statusName.parent != null) {
                (statusName.parent as ViewGroup).removeView(statusName)
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Add new board")
                .setView(statusName)
                .setPositiveButton("Add") { dialog, which ->
                    val name = statusName.text.toString()
                    if (name.isEmpty()) {
                        Toast.makeText(requireContext(), "The board name cannot be empty",
                            Toast.LENGTH_SHORT).show()
                    } else if (map.contains(name)) {
                        Toast.makeText(requireContext(), "The status with same name already exist",
                            Toast.LENGTH_SHORT).show()
                    } else {
                        taskStatusViewModel.create(TaskStatusCreateModel(name, id!!))
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}