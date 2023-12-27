
package com.example.taskermobile.activities.project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.taskermobile.R
import com.example.taskermobile.model.project.ProjectUpdateModel
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.viewmodels.ProjectsViewModel
import androidx.navigation.fragment.findNavController

class ProjectUpdateActivity: Fragment() {
    private val viewModel: ProjectsViewModel by viewModels()

    private lateinit var loadingIndicator: ProgressBar
    private lateinit var projectTitleHolder: EditText
    private lateinit var changeProjectNameButton: Button



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.project_update, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val projectId = arguments?.getString("PROJECT_ID") ?: throw IllegalArgumentException("Project ID required")

        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        projectTitleHolder = view.findViewById(R.id.projectName)
        changeProjectNameButton = view.findViewById(R.id.update)

        setupObservers(projectId)
    }

    private fun setupObservers(projectId: String) {
        viewModel.getById(projectId)

        viewModel.projectGetByIdResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }
                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    apiResponse.data?.let { project ->
                        projectTitleHolder.setText(project.title)
                        setupClickListeners(projectId, project.title)
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

        viewModel.projectUpdateResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }

                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    val bundle = bundleOf("PROJECT_ID" to projectId)
                    findNavController().navigate(R.id.projectsPageFragment, bundle)
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

    private fun setupClickListeners(projectId: String, title: String?) {
        changeProjectNameButton.setOnClickListener {
            val name: String = projectTitleHolder.text.toString().trim()

            if (title?.isEmpty() == true || title == name || name.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "The new project title should not be empty and repeat old one",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                viewModel.update(
                    ProjectUpdateModel(
                        id = projectId,
                        title = name
                    )
                )
            }
        }
    }
}