package com.example.taskermobile.activities.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.taskermobile.R
import com.example.taskermobile.model.TaskPriority
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.viewmodels.TaskViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TaskDetailFragment : Fragment() {
    private val taskViewModel by viewModel<TaskViewModel>()

    private lateinit var loadingIndicator: ProgressBar

    private lateinit var editButton: Button
    private lateinit var deleteButton: Button

    private lateinit var title: TextView
    private lateinit var description: TextView
    private lateinit var assigne: TextView
    private lateinit var creator: TextView
    private lateinit var taskStatus: TextView
    private lateinit var release: TextView
    private lateinit var priority: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.task_detail_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val taskId = arguments?.getString("TASK_ID")

        if (taskId == null) {
            Toast.makeText(
                requireContext(), "The task id was not found",
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigate(R.id.action_taskDetailFragment_to_kanbanBoardDetailFragment)
            return
        }

        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        editButton = view.findViewById(R.id.updateTask)
        deleteButton = view.findViewById(R.id.deleteTask)

        title = view.findViewById(R.id.nameView)
        taskStatus = view.findViewById(R.id.taskStatusView)
        creator = view.findViewById(R.id.creatorView)
        assigne = view.findViewById(R.id.assigneView)
        release = view.findViewById(R.id.releaseView)
        description = view.findViewById(R.id.descriptionView)
        priority = view.findViewById(R.id.priorityView)

        taskViewModel.get(taskId)
        setUpObservers(taskId)
    }

    private fun setUpObservers(taskId: String) {
        taskViewModel.taskGetResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }
                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    val priorityMap = listOf("None", "Low", "Medium", "High", "Urgent")

                    title.text = if(apiResponse.data!!.title.isNullOrEmpty())
                        "without title"
                    else apiResponse.data.title
                    description.text = if(apiResponse.data.description.isNullOrEmpty())
                        "without description"
                    else apiResponse.data.description
                    assigne.text = apiResponse.data.assignee?.title ?: "not assigned"
                    creator.text = apiResponse.data.creator?.title ?: "without a creator"
                    priority.text = priorityMap[apiResponse.data.priority]
                    taskStatus.text = apiResponse.data.taskStatus?.title ?: "without a status"
                    release.text = apiResponse.data.release?.title ?: "without a release"

                    addEventListeners(taskId)
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

    private fun addEventListeners(taskId: String) {
        editButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_taskDetailFragment_to_taskEditFragment,
                bundleOf("TASK_ID" to id)
            )
        }

        deleteButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Confirm status deletion")
                .setPositiveButton("Delete") { dialog, which ->
                    taskViewModel.delete(taskId)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}