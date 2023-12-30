package com.example.taskermobile.activities.taskstatuses

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
import com.example.taskermobile.R
import com.example.taskermobile.model.kanbanboard.KanbanBoardUpdateModel
import com.example.taskermobile.model.taskstatus.TaskStatusModel
import com.example.taskermobile.model.taskstatus.TaskStatusUpdateModel
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.viewmodels.KanbanBoardViewModel
import com.example.taskermobile.viewmodels.TaskStatusViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TaskStatusDetailFragment : Fragment() {
    private val kanbanBoardViewModel by viewModel<KanbanBoardViewModel>()
    private val taskStatusViewModel by viewModel<TaskStatusViewModel>()

    private lateinit var loadingIndicator: ProgressBar
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    private lateinit var title: TextView
    private lateinit var editTitle: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.task_status_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val boardId = arguments?.getString("TASK_STATUS_ID")

        if (boardId == null) {
            Toast.makeText(requireContext(), "The task status was not found",
                Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_taskStatusDetailFragment_to_taskStatusListFragment)
            return
        }

        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        title = view.findViewById(R.id.statusName)
        editButton = view.findViewById(R.id.updateStatusName)
        deleteButton = view.findViewById(R.id.deleteStatusName)
        editTitle = EditText(requireContext())

        setUpObservers(boardId)
    }

    private fun setUpObservers(boardId: String) {
        taskStatusViewModel.getById(boardId)

        taskStatusViewModel.taskStatusGetByIdResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }

                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    apiResponse.data?.let { taskStatus ->
                        loadingIndicator.visibility = View.GONE
                        title.text = taskStatus.name

                        addEventListeners(taskStatus)
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

        taskStatusViewModel.taskStatusUpdateResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }
                is ApiResponse.Success -> {
                    taskStatusViewModel.getById(boardId)
                    editTitle.text.clear()
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

        taskStatusViewModel.taskStatusDeleteResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }
                is ApiResponse.Success -> {
                    findNavController().navigate(R.id.action_taskStatusDetailFragment_to_taskStatusListFragment,
                        bundleOf("KANBAN_BOARD_ID" to boardId))
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

    private fun addEventListeners(taskStatus: TaskStatusModel) {
        editButton.setOnClickListener {
            editTitle.setText(title.text)

            if (editTitle.parent != null) {
                (editTitle.parent as ViewGroup).removeView(editTitle)
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Update task status")
                .setView(editTitle)
                .setPositiveButton("Update") { dialog, which ->
                    val name = editTitle.text.toString()
                    if (name.isEmpty()) {
                        Toast.makeText(requireContext(), "The name cannot be empty",
                            Toast.LENGTH_SHORT).show()
                    } else if (name == taskStatus.name) {
                        Toast.makeText(requireContext(), "The name is the same as previous",
                            Toast.LENGTH_SHORT).show()
                    }
                    else {
                        taskStatusViewModel.update(TaskStatusUpdateModel(taskStatus.id!!,
                            name,
                            taskStatus.kanbanBoardId!!))
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        deleteButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Confirm status deletion")
                .setPositiveButton("Delete") { dialog, which ->
                    taskStatusViewModel.delete(taskStatus.id!!)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}