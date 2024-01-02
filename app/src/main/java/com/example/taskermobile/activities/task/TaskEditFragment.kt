package com.example.taskermobile.activities.task

import SharedPreferencesService
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.taskermobile.R
import com.example.taskermobile.model.TaskPriority
import com.example.taskermobile.model.kanbanboard.KanbanBoardModel
import com.example.taskermobile.model.release.ReleasePreviewModel
import com.example.taskermobile.model.task.TaskCreateModel
import com.example.taskermobile.model.task.TaskModel
import com.example.taskermobile.model.task.TaskUpdateModel
import com.example.taskermobile.model.taskstatus.TaskStatusBoardModel
import com.example.taskermobile.model.taskstatus.TaskStatusModel
import com.example.taskermobile.model.user.MemberModel
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.getIdFromToken
import com.example.taskermobile.viewadapters.ReleaseAdapter
import com.example.taskermobile.viewmodels.KanbanBoardViewModel
import com.example.taskermobile.viewmodels.ProjectsViewModel
import com.example.taskermobile.viewmodels.ReleasesPageViewModel
import com.example.taskermobile.viewmodels.TaskViewModel
import com.example.taskermobile.viewmodels.TokenViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class TaskEditFragment : Fragment() {
    private val tokenViewModel: TokenViewModel by viewModel()
    private val kanbanBoardModel by viewModel<KanbanBoardViewModel>()
    private val projectModel by viewModel<ProjectsViewModel>()
    private val releaseModel by viewModel<ReleasesPageViewModel>()
    private val taskModel by viewModel<TaskViewModel>()

    private val sharedPreferences: SharedPreferencesService by inject()

    private lateinit var currentKanbanBoard: KanbanBoardModel
    private lateinit var projectUsers: List<MemberModel>
    private lateinit var taskStatuses: List<TaskStatusBoardModel>
    private lateinit var releases: List<ReleasePreviewModel>
    private lateinit var projectId: String
    private lateinit var taskId: String
    private lateinit var kanbanBoardId: String
    private lateinit var taskTitle: EditText
    private lateinit var taskDescription: EditText
    private lateinit var prioritySpinner: Spinner
    private lateinit var updateButton: Button
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var overlayView: View
    private var selectedPriority = "NONE"
    private var selectedStatus: String? = null
    private var selectedAssignee: String? = null
    private var selectedRelease: String? = null

    private var priorityList = listOf("NONE", "LOW", "MEDIUM", "HIGH", "URGENT")
    private var statusList : MutableList<String?> = emptyList<String?>().toMutableList()
    private var assigneeList : MutableList<String> = emptyList<String>().toMutableList()
    private var releaseList : MutableList<String> = emptyList<String>().toMutableList()
    private var currentTask : TaskModel? = null

    private lateinit var releaseSpinner: Spinner
    private lateinit var statusSpinner: Spinner
    private lateinit var assigneeSpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.task_update, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskTitle = view.findViewById(R.id.newTaskTitle)
        taskDescription = view.findViewById(R.id.newTaskDescription)
        prioritySpinner = view.findViewById(R.id.newTaskPriority)
        statusSpinner = view.findViewById(R.id.newTaskStatus)
        assigneeSpinner = view.findViewById(R.id.newTaskAssignee)
        releaseSpinner = view.findViewById(R.id.newTaskRelease)
        updateButton = view.findViewById(R.id.updateTaskButton)
        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        overlayView = view.findViewById(R.id.overlayView)

        projectId = sharedPreferences.retrieveData("PROJECT_ID").toString()
        taskId = arguments?.getString("TASK_ID")!!
        kanbanBoardId = sharedPreferences.retrieveData("lastKanbanBoard").toString()
        projectUsers = emptyList()

        setUpPrioritySpinner()
        setUpObservers()
        taskModel.get(taskId)
    }

    private fun setUpUserSpinner() {
        assigneeList = projectUsers.map { u -> u.title }.toMutableList().apply {
            add(0, "-")
        }
        val assigneeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, assigneeList)
        assigneeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        assigneeSpinner.adapter = assigneeAdapter

        assigneeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedAssignee = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedAssignee = null
            }
        }
    }

    private fun setUpStatusSpinner() {
        statusList = taskStatuses.map { u -> u.name }.toMutableList().apply {
            add(0, "-")
        }
        val statusAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusList)
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        statusSpinner.adapter = statusAdapter

        statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedStatus = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedStatus = null
            }
        }

        statusSpinner.setSelection(statusList.indexOf(currentTask?.taskStatus?.title))
    }

    private fun setUpReleaseSpinner() {
        releaseList = releases.map { u -> u.title }.toMutableList().apply {
            add(0, "-")
        }
        val releaseAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, releaseList)
        releaseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        releaseSpinner.adapter = releaseAdapter

        releaseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedRelease = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedRelease = null
            }
        }
        setUpListeners()
    }

    private fun setUpPrioritySpinner(){
        priorityList = listOf("NONE", "LOW", "MEDIUM", "HIGH", "URGENT")
        val priorityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, priorityList)
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        prioritySpinner.adapter = priorityAdapter
        prioritySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedPriority = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedPriority = "NONE"
            }
        }
    }

    private fun setUpObservers() {
        tokenViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            updateButton.isEnabled = !isLoading

            kanbanBoardModel.getById(kanbanBoardId)
        })

        kanbanBoardModel.kanbanBoardResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    showLoading()
                }
                is ApiResponse.Success -> {
                    hideLoading()
                    apiResponse.data?.let { board ->
                        currentKanbanBoard = board

                        taskStatuses = board.columns ?: emptyList()

                        projectModel.getProjectMembers(board.projectId!!)
                        setUpStatusSpinner()
                    }
                }
                is ApiResponse.Failure -> {
                    hideLoading()
                    Toast.makeText(
                        requireContext(),
                        "Failed to load kanban board: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        releaseModel.releasesResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    showLoading()
                }
                is ApiResponse.Success -> {
                    hideLoading()
                    apiResponse.data?.let { data ->
                        releases = data
                        setUpReleaseSpinner()

                        releaseSpinner.setSelection(releaseList.indexOf(currentTask?.release?.title))
                    }
                }
                is ApiResponse.Failure -> {
                    hideLoading()
                    Toast.makeText(
                        requireContext(),
                        "Network error: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        projectModel.projectMembersResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    showLoading()
                }
                is ApiResponse.Success -> {
                    hideLoading()
                    apiResponse.data?.let { data ->
                        projectUsers = data
                        setUpUserSpinner()
                        releaseModel.getAll(currentKanbanBoard.projectId!!)

                        assigneeSpinner.setSelection(assigneeList.indexOf(currentTask?.assignee?.title))
                    }
                }
                is ApiResponse.Failure -> {
                    hideLoading()
                    Toast.makeText(
                        requireContext(),
                        "Failed to load kanban board: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        taskModel.taskUpdateResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    showLoading()
                }
                is ApiResponse.Success -> {
                    hideLoading()
                    Toast.makeText(
                        requireContext(),
                        "Task ${apiResponse.data?.title} updated",
                        Toast.LENGTH_LONG
                    ).show()

                    findNavController().navigate(R.id.action_taskEditFragment_to_taskDetailFragment,
                        bundleOf("TASK_ID" to apiResponse.data!!.id),
                        NavOptions.Builder()
                            .setPopUpTo(R.id.taskEditFragment, true)
                            .build())
                }
                is ApiResponse.Failure -> {
                    hideLoading()
                    Toast.makeText(
                        requireContext(),
                        "Creation failed: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        taskModel.taskGetResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    showLoading()
                }
                is ApiResponse.Success -> {
                    hideLoading()
                    currentTask = apiResponse.data
                    taskTitle.setText(currentTask?.title)
                    taskDescription.setText(currentTask?.description)
                    prioritySpinner.setSelection(currentTask?.priority!!)
                }
                is ApiResponse.Failure -> {
                    hideLoading()
                    Toast.makeText(
                        requireContext(),
                        "Task get failed: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun setUpListeners() {
        updateButton.setOnClickListener{
            val selectedTaskStatusModel = taskStatuses.find { it.name == selectedStatus }
            val selectedReleaseModel = releases.find { it.title == selectedRelease }
            val selectedAssigneeModel = projectUsers.find { it.title == selectedAssignee }
            val task = TaskUpdateModel(
                id = taskId,
                title = taskTitle.text.toString(), // +
                description = taskDescription.text.toString(), // +
                priority = enumValueOf<TaskPriority>(selectedPriority).ordinal, // +
                statusId = selectedTaskStatusModel?.id,
                releaseId = selectedReleaseModel?.id, // -
                assigneeId = selectedAssigneeModel?.id // +

            )
            taskModel.update(task)
        }
    }

    private fun showLoading() {
        loadingIndicator.visibility = View.VISIBLE
        overlayView.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        loadingIndicator.visibility = View.GONE
        overlayView.visibility = View.GONE
    }
}