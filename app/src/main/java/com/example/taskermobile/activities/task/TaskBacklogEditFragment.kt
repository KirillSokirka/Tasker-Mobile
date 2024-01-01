package com.example.taskermobile.activities.task

import SharedPreferencesService
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.taskermobile.R
import com.example.taskermobile.model.TaskPriority
import com.example.taskermobile.model.kanbanboard.KanbanBoardModel
import com.example.taskermobile.model.task.TaskModel
import com.example.taskermobile.model.task.TaskUpdateModel
import com.example.taskermobile.model.task.TaskUpdateStatusModel
import com.example.taskermobile.model.taskstatus.TaskStatusBoardModel
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.viewmodels.KanbanBoardViewModel
import com.example.taskermobile.viewmodels.ProjectsViewModel
import com.example.taskermobile.viewmodels.TaskViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class TaskBacklogEditFragment : Fragment() {

    private val sharedPreferencesService: SharedPreferencesService by inject()
    private val taskViewModel by viewModel<TaskViewModel>()
    private val projectViewModel by viewModel<ProjectsViewModel>()
    private val boardViewModel by viewModel<KanbanBoardViewModel>()

    private lateinit var hint: TextView

    private lateinit var loadingIndicator: ProgressBar
    private lateinit var statusSpinner: Spinner
    private lateinit var boardSpinner: Spinner
    private lateinit var sSelect: TextView
    private lateinit var bSelect: TextView
    private lateinit var task: TaskModel
    private lateinit var boardId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.task_backlog_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val taskId = arguments?.getString("TASK_ID")

        if (taskId == null) {
            findNavController().navigate(R.id.action_backlogPageFragment_to_projectsPageFragment)
        }

        hint = view.findViewById(R.id.hint)
        boardId = ""
        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        boardSpinner = view.findViewById(R.id.boardSelector)
        statusSpinner = view.findViewById(R.id.taskStatusSelector)
        bSelect = view.findViewById(R.id.bSelect)
        sSelect = view.findViewById(R.id.sSelect)

        taskViewModel.get(taskId!!)

        setUpObservers()
    }

    private fun setUpObservers() {
        taskViewModel.taskGetResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }
                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    task = apiResponse.data!!
                    projectViewModel.getById(apiResponse.data.projectId)
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

        projectViewModel.projectGetByIdResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }
                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    setUpKanbanBoardSpinner(apiResponse.data?.kanbanBoards)
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

        boardViewModel.kanbanBoardResponse.observe(viewLifecycleOwner) { apiResponse ->
            Log.d("TaskBacklogEditFragment", "API Response: $apiResponse")
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }
                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    boardId = apiResponse.data?.id!!
                    setUpStatusSpinner(apiResponse.data.columns)
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

        taskViewModel.taskUpdateResponse.observe(viewLifecycleOwner) {apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }
                is ApiResponse.Success -> {
                    findNavController().navigate(
                        R.id.action_taskBacklogEditFragment_to_kanbanBoardDetailFragment,
                        bundleOf("KANBAN_BOARD_ID" to boardId)
                    )
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

    private fun setUpKanbanBoardSpinner(kanbanBoards: List<KanbanBoardModel>?) {
        if (kanbanBoards.isNullOrEmpty()) {
            hint.visibility = View.VISIBLE
            hint.text = "There is no available boards in project. Add them and comeback here"
            return
        } else {
            bSelect.visibility = View.VISIBLE
            boardSpinner.visibility = View.VISIBLE
        }

        val statusList = kanbanBoards.map { u -> u.title }.toMutableList().apply {
            add(0, "Select board")
        }
        val statusAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusList)
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        boardSpinner.adapter = statusAdapter

        boardSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    boardViewModel.getById(kanbanBoards[position - 1].id!!)

                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    private fun setUpStatusSpinner(columns: List<TaskStatusBoardModel>?) {
        if (columns.isNullOrEmpty()) {
            hint.visibility = View.VISIBLE
            hint.text = "There is no available task statuses in that board. Add them and comeback here"
            return
        } else {
            hint.visibility = View.GONE
            sSelect.visibility = View.VISIBLE
            statusSpinner.visibility = View.VISIBLE
        }

        val statusList = columns.map { u -> u.name }.toMutableList().apply {
            add(0, "Select status")
        }
        val statusAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusList)
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        statusSpinner.adapter = statusAdapter

        statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    taskViewModel.update(TaskUpdateModel(
                        id = task.id,
                        title = task.title,
                        description = task.description,
                        priority = task.priority,
                        statusId = columns[position - 1].id!!,
                        releaseId = task.release?.id,
                        assigneeId = task.assignee?.id
                    ))
                    Toast.makeText(requireContext(),
                        "Status changed to: ${columns[position - 1].name!!}",
                        Toast.LENGTH_LONG).show()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }
}