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
    private var selectedPriority = "NONE"

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
        createButton = view.findViewById(R.id.createTaskButton)
        loadingIndicator = view.findViewById(R.id.loadingIndicator)

        kanbanBoardId = sharedPreferences.retrieveData("lastKanbanBoard").toString()

        priority.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedPriority = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedPriority = "NONE"
            }
        }

        setUpObservers()
    }

    private fun setUpObservers() {
        tokenViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            createButton.isEnabled = !isLoading

            kanbanBoardModel.getById(kanbanBoardId)
        })

        kanbanBoardModel.kanbanBoardResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }
                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    apiResponse.data?.let { board ->
                        currentKanbanBoard = board

                        taskStatuses = board.columns ?: emptyList()

                        projectModel.getProjectMembers(board.projectId!!)
                    }
                }
                is ApiResponse.Failure -> {
                    loadingIndicator.visibility = View.GONE
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
                    loadingIndicator.visibility = View.VISIBLE
                }
                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    apiResponse.data?.let { data ->
                        projectUsers = data

                        val temp = 0
                    }
                }
                is ApiResponse.Failure -> {
                    loadingIndicator.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Failed to load kanban board: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

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
}