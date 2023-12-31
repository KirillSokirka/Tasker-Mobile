package com.example.taskermobile.activities.project

import android.content.Intent
import com.example.taskermobile.viewadapters.ProjectAdapter
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
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.R
import com.example.taskermobile.model.project.ProjectCreateModel
import com.example.taskermobile.model.task.TaskBoardPreviewModel
import com.example.taskermobile.model.task.TaskPreviewModel
import com.example.taskermobile.model.taskstatus.TaskStatusBoardModel
import com.example.taskermobile.model.taskstatus.TaskStatusModel
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.eventlisteners.OnItemClickListener
import com.example.taskermobile.utils.getIdFromToken
import com.example.taskermobile.viewmodels.ProjectsViewModel
import com.example.taskermobile.viewmodels.TokenViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProjectsPageFragment : Fragment() {
    private val viewModel: ProjectsViewModel by viewModel()
    private val tokenViewModel: TokenViewModel by viewModel<TokenViewModel>()
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var overlayView: View
    private lateinit var titleEdit : EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.projects_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        overlayView = view.findViewById(R.id.overlayView)
        viewModel.getAll()

        val createProjectButton: Button = view.findViewById(R.id.buttonCreate)

        tokenViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            createProjectButton.isEnabled = !isLoading
        }

//        createProjectButton.setOnClickListener {
//            findNavController().navigate(
//                R.id.action_projectsPageFragment_to_projectCreateFragment)
//        }
        titleEdit = EditText(requireContext())
        createProjectButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Create project")
                .setView(titleEdit)
                .setPositiveButton("Create") { dialog, which ->
                    val title = titleEdit.text.toString()
                    if (title.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "The project name cannot be empty",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val userId = getIdFromToken(tokenViewModel.token.value?.token.toString())
                        viewModel.create(ProjectCreateModel(title, userId!!))
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        viewModel.projectCreateResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }

                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Project ${titleEdit.text} created",
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.getAll()
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

        viewModel.projectsResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }

                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    val recyclerView: RecyclerView = view.findViewById(R.id.recyclerview)
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    recyclerView.adapter = ProjectAdapter(apiResponse.data, object :
                        OnItemClickListener {
                        override fun onItemClick(id: String) {
                            findNavController().navigate(
                                R.id.action_projectsPageFragment_to_projectDetailFragment,
                                bundleOf("PROJECT_ID" to id))
                        }

                        override fun onItemLongClick(task: TaskBoardPreviewModel,
                            allStatuses: List<TaskStatusBoardModel>,
                            view: View?) { }
                    })
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
}

