
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
import com.example.taskermobile.activities.release.ReleasesPageActivity
import com.example.taskermobile.model.project.ProjectModel
import com.example.taskermobile.model.project.ProjectUpdateModel
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.viewmodels.ProjectsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProjectUpdateActivity: AppCompatActivity() {
    private val viewModel: ProjectsViewModel by viewModel()

    private lateinit var loadingIndicator: ProgressBar
    private lateinit var projectTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val projectId = intent.getStringExtra("PROJECT_ID").toString()

        setContentView(R.layout.project_update)

        loadingIndicator = findViewById(R.id.loadingIndicator)

        val projectTitleHolder: EditText = findViewById(R.id.projectName)

        viewModel.getById(projectId)

        viewModel.projectGetByIdResponse.observe(this) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }

                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    apiResponse.data?.let { project ->
                        projectTitleHolder.setText(project.title)
                        projectTitle = project.title.toString()
                    }
                }

                is ApiResponse.Failure -> {
                    loadingIndicator.visibility = View.GONE
                    Toast.makeText(
                        this@ProjectUpdateActivity,
                        "Network error: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        val changeProjectNameButton: Button = findViewById(R.id.update)

        changeProjectNameButton.setOnClickListener {
            val name: String = projectTitleHolder.text.toString().trim()

            if (projectTitle.isEmpty() || projectTitle == name || name.isEmpty()) {
                Toast.makeText(
                    this@ProjectUpdateActivity,
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

        viewModel.projectUpdateResponse.observe(this) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }

                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    val intent = Intent(this@ProjectUpdateActivity, ProjectDetailActivity::class.java)
                    intent.putExtra("PROJECT_ID", projectId)
                    startActivity(intent)
                    finish()
                }

                is ApiResponse.Failure -> {
                    loadingIndicator.visibility = View.GONE
                    Toast.makeText(
                        this@ProjectUpdateActivity,
                        "Network error: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        }
    }
}