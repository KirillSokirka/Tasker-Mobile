package com.example.taskermobile.activities.project

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskermobile.R
import com.example.taskermobile.model.project.ProjectCreateModel
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.getIdFromToken
import com.example.taskermobile.viewmodels.ProjectsViewModel
import com.example.taskermobile.viewmodels.TokenViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProjectCreateActivity : AppCompatActivity() {
    private val tokenViewModel: TokenViewModel by viewModel()
    private val viewModel: ProjectsViewModel by viewModel()

    private lateinit var loadingIndicator: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.project_create)
        loadingIndicator = findViewById(R.id.loadingIndicator)

        val projectName: EditText = findViewById(R.id.projectName)
        val createButton: Button = findViewById(R.id.createProjectButton)

        tokenViewModel.isLoading.observe(this) { isLoading ->
            createButton.isEnabled = !isLoading
        }

        createButton.setOnClickListener{
            val name: String = projectName.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(
                    this@ProjectCreateActivity,
                    "The project name cannot be empty",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val userId = getIdFromToken(tokenViewModel.token.value?.token.toString())!!
                viewModel.create(ProjectCreateModel(name, userId))
            }
        }

        viewModel.projectCreateResponse.observe(this) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }

                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    startActivity(
                        Intent(this@ProjectCreateActivity,
                        ProjectsPageActivity::class.java)
                    )
                    finish()
                }

                is ApiResponse.Failure -> {
                    loadingIndicator.visibility = View.GONE
                    Toast.makeText(
                        this@ProjectCreateActivity,
                        "Network error: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}