package com.example.taskermobile.activities.users

import SharedPreferencesService
import android.content.Intent
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
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.taskermobile.LoginActivity
import com.example.taskermobile.R
import com.example.taskermobile.model.TaskPriority
import com.example.taskermobile.model.auth.ChangePasswordModel
import com.example.taskermobile.model.kanbanboard.KanbanBoardModel
import com.example.taskermobile.model.kanbanboard.TaskStatusModel
import com.example.taskermobile.model.task.TaskCreateModel
import com.example.taskermobile.model.task.TaskModel
import com.example.taskermobile.model.user.MemberModel
import com.example.taskermobile.model.user.UserModel
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.getEmailFromToken
import com.example.taskermobile.utils.getIdFromToken
import com.example.taskermobile.utils.getUsernameFromToken
import com.example.taskermobile.viewadapters.KanbanBoardAdapter
import com.example.taskermobile.viewmodels.KanbanBoardViewModel
import com.example.taskermobile.viewmodels.ProjectsViewModel
import com.example.taskermobile.viewmodels.TokenViewModel
import com.example.taskermobile.viewmodels.UserViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class TaskCreateFragment : Fragment() {
    private val tokenViewModel: TokenViewModel by viewModel()
    private val kanbanBoardModel by viewModel<KanbanBoardViewModel>()
    private val projectModel by viewModel<ProjectsViewModel>()

    private val sharedPreferences: SharedPreferencesService by inject()

    private lateinit var currentKanbanBoard: KanbanBoardModel
    private lateinit var projectUsers: List<MemberModel>
    private lateinit var taskStatuses: List<TaskStatusModel>
    private lateinit var kanbanBoardId: String
    private lateinit var taskTitle: EditText
    private lateinit var taskDescription: EditText
    private lateinit var priority: Spinner
    private lateinit var createButton: Button
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var overlayView: View
    private var selectedPriority = "NONE"
    private lateinit var selectedStatus: String
    private var selectedAssignee: String? = null

    private lateinit var statusSpiner: Spinner
    private lateinit var assigneeSpiner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.task_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskTitle = view.findViewById(R.id.createTaskTitle)
        taskDescription = view.findViewById(R.id.createTaskDescription)
        priority = view.findViewById(R.id.createTaskPriority)
        statusSpiner = view.findViewById(R.id.createTaskStatus)
        assigneeSpiner = view.findViewById(R.id.createTaskAssignee)
        createButton = view.findViewById(R.id.createTaskButton)
        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        overlayView = view.findViewById(R.id.overlayView)

        kanbanBoardId = sharedPreferences.retrieveData("lastKanbanBoard").toString()
        projectUsers = emptyList()

        setUpObservers()
    }

    private fun setUpSpinners(){
        priority.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedPriority = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedPriority = "NONE"
            }
        }

        val assigneeList = projectUsers.map { u -> u.title }.toMutableList()
        val assigneeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, assigneeList)
        assigneeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        assigneeSpiner.adapter = assigneeAdapter

        assigneeSpiner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedAssignee = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedAssignee = null
            }
        }
    }

    private fun setUpObservers() {
        tokenViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            createButton.isEnabled = !isLoading

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

        projectModel.projectMembersResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    showLoading()
                }
                is ApiResponse.Success -> {
                    hideLoading()
                    apiResponse.data?.let { data ->
                        projectUsers = data
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

        setUpSpinners()
    }

    private fun setUpListeners() {
        createButton.setOnClickListener{
            val idFromJwt = getIdFromToken(tokenViewModel.token.value!!.toString())!!
            val task = TaskCreateModel(
                title = taskTitle.text.toString(),
                description = taskDescription.text.toString(),
                priority = enumValueOf<TaskPriority>(selectedPriority),
                creatorId = idFromJwt,
                projectId = currentKanbanBoard.projectId
            )
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