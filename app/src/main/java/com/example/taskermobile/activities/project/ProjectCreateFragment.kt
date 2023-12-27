package com.example.taskermobile.activities.project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.taskermobile.R
import com.example.taskermobile.model.project.ProjectCreateModel
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.getIdFromToken
import com.example.taskermobile.viewmodels.ProjectsViewModel
import com.example.taskermobile.viewmodels.TokenViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProjectCreateFragment : Fragment() {
    private val tokenViewModel: TokenViewModel by viewModel()
    private val viewModel: ProjectsViewModel by viewModel()

    private lateinit var loadingIndicator: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.project_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        val projectName : EditText = view.findViewById(R.id.projectName)
        val createButton : EditText = view.findViewById(R.id.createProjectButton)

        tokenViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            createButton.isEnabled = !isLoading
        }

        createButton.setOnClickListener {
            val name: String = projectName.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "The project name cannot be empty",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val userId = getIdFromToken(tokenViewModel.token.value?.token.toString()) ?: return@setOnClickListener
                viewModel.create(ProjectCreateModel(name, userId))
            }
        }

        viewModel.projectCreateResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }

                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    findNavController().navigate(R.id.action_projectCreateFragment_to_projectsPageFragment)
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