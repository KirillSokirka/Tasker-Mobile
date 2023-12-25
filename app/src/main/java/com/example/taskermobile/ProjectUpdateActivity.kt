package com.example.taskermobile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.model.ProjectModel
import com.example.taskermobile.model.ProjectUpdateModel
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.viewmodels.ProjectsPageViewModel
import com.example.taskermobile.viewmodels.TokenViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.jar.Attributes.Name

class ProjectUpdateActivity: AppCompatActivity() {
    private val tokenViewModel: TokenViewModel by viewModel()
    private val projectUpdateViewModel: ProjectsPageViewModel by viewModel()

    private lateinit var loadingIndicator: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val projectId = intent.getStringExtra("projectId")

        setContentView(R.layout.project_manage_activity)
        loadingIndicator = findViewById(R.id.loadingIndicator)
        val projectTitleHolder: EditText = findViewById(R.id.projectName)
        projectUpdateViewModel.getById(projectId!!)

        projectUpdateViewModel.projectGetByIdResponse.observe(this) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }

                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    projectTitleHolder.hint = apiResponse.data!!.title

                }

                is ApiResponse.Failure -> {
                    loadingIndicator.visibility = View.GONE
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                }
            }
        }
//        projectTitleHolder.hint = projectUpdateViewModel.projectGetByIdResponse.


        val changeProjectNameButton: Button = findViewById(R.id.changeProjectName)

        changeProjectNameButton.setOnClickListener {
            projectUpdateViewModel.update(
                ProjectUpdateModel(
                    id = projectId,
                    title = findViewById(R.id.projectName)
                )
            )
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}