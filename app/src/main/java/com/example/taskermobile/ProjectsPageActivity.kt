package com.example.taskermobile

import ProjectPreviewRecyclerViewAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.service.ProjectApiService
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.viewmodels.ProjectsPageViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ProjectsPageActivity : AppCompatActivity() {

    private val viewModel: ProjectsPageViewModel by viewModel()
    private lateinit var loadingIndicator: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.projects)
        loadingIndicator = findViewById(R.id.loadingIndicator)

        viewModel.getAll()

        viewModel.projectsResponse.observe(this) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }

                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    val recyclerView: RecyclerView = findViewById(R.id.recyclerview)
                    recyclerView.layoutManager = LinearLayoutManager(this)
                    recyclerView.adapter = ProjectPreviewRecyclerViewAdapter(apiResponse.data)
                }

                is ApiResponse.Failure -> {
                    loadingIndicator.visibility = View.GONE
                    Toast.makeText(
                        this@ProjectsPageActivity,
                        "Network error: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
