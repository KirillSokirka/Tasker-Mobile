package com.example.taskermobile.activities.project

import SharedPreferencesService
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.taskermobile.MainActivity
import com.example.taskermobile.R
import com.example.taskermobile.model.project.ProjectModel
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.getIdFromToken
import com.example.taskermobile.viewmodels.ProjectsViewModel
import com.example.taskermobile.viewmodels.TokenViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProjectDetailFragment : Fragment() {

    private val viewModel by viewModel<ProjectsViewModel>()
    private val tokenModel by viewModel<TokenViewModel>()

    private val sharedPreferences: SharedPreferencesService by inject()

    private lateinit var loadingIndicator: ProgressBar
    private lateinit var title: TextView
    private lateinit var editProjectButton: Button
    private lateinit var releasesButton: Button
    private lateinit var manageUsersButton: Button
    private lateinit var spinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.project_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val projectIdFromArgs = arguments?.getString("PROJECT_ID")
        val lastProjectActive = sharedPreferences.retrieveData("lastProjectActive")

        if (lastProjectActive != projectIdFromArgs) {
            sharedPreferences.saveData("lastKanbanBoard", null)
        }

        val projectId = projectIdFromArgs ?: lastProjectActive
        if (projectId == null) {
            findNavController().navigate(R.id.action_projectDetailFragment_to_projectsPageFragment)
            return
        } else {
            sharedPreferences.saveData("lastProjectActive", projectId)
        }

        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        title = view.findViewById(R.id.title)
        editProjectButton = view.findViewById(R.id.editProject)
        releasesButton = view.findViewById(R.id.releasesInfo)
        manageUsersButton = view.findViewById(R.id.manageUsersButton)
        spinner = view.findViewById(R.id.kanbanBoardsSpinner)

        setupObservers(projectId)
    }

    private fun setupObservers(projectId: String) {
        tokenModel.token.observe(viewLifecycleOwner) { tokenValue ->
            if (tokenValue != null) {
                viewModel.getById(projectId)
            } else {
                loadingIndicator.visibility = View.VISIBLE
            }
        }

        viewModel.projectGetByIdResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }

                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    apiResponse.data?.let { project ->

                        val token = tokenModel.token.value

                        val userId = getIdFromToken(token!!.token)

                        val users = (project.assignedUsers ?: emptyList()) + (project.adminProjects
                            ?: emptyList())

                        if (!users.contains(userId)) {
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                        }

                        setupClickListeners(project, userId!!)

                        title.text = project.title

                        val defaultTitle = "Select a Kanban Board"
                        val kanbanBoardNames = mutableListOf(defaultTitle)
                        project.kanbanBoards?.map { kanbanBoardNames.add(it.title.toString()) }
                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, kanbanBoardNames)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                        spinner.adapter = adapter

                        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                                if (view != null) {
                                    if (position == 0) {
                                        spinner.setSelection(0)
                                    } else {
                                        val selectedBoardId =
                                            project.kanbanBoards?.get(position - 1)?.id

                                        findNavController().navigate(
                                            R.id.action_projectDetailFragment_to_kanbanBoardDetailFragment,
                                            bundleOf("KANBAN_BOARD_ID" to selectedBoardId)
                                        )
                                    }
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                spinner.setSelection(0)
                            }
                        }
                    }
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

    private fun setupClickListeners(project: ProjectModel, userId: String) {
        editProjectButton.setOnClickListener {
            findNavController().navigate(R.id.action_projectDetailFragment_to_projectUpdateFragment, Bundle().apply {
                putString("PROJECT_ID", project.id)
            })
        }

        releasesButton.setOnClickListener{
            findNavController().navigate(R.id.action_projectDetailFragment_to_releasesPageFragment, Bundle().apply {
                putString("PROJECT_ID", project.id)
            })
        }

        manageUsersButton.setOnClickListener {
            val destinationId = if (project.adminProjects?.contains(userId) == true) {
                R.id.action_projectDetailFragment_to_userListFragment
            } else {
                R.id.action_projectDetailFragment_to_userListFragment
            }

            findNavController().navigate(destinationId, Bundle().apply {
                putString("PROJECT_ID", project.id)
            })
        }
    }
}