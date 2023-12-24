package com.example.taskermobile

import BacklogRecyclerViewAdapter
import ProjectPreviewRecyclerViewAdapter
import ReleasePreviewRecyclerViewAdapter
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
import com.example.taskermobile.viewmodels.BacklogPageViewModel
import com.example.taskermobile.viewmodels.ProjectsPageViewModel
import com.example.taskermobile.viewmodels.ReleasesPageViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class BacklogPageActivity : AppCompatActivity() {

    private val viewModel: BacklogPageViewModel by viewModel()
    private lateinit var loadingIndicator: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.backlog)
        loadingIndicator = findViewById(R.id.loadingIndicator)

        val projectId = intent.getStringExtra("projectId")
        if (projectId != null) {
            viewModel.getAll(projectId)
        } else {
            Toast.makeText(
                this@BacklogPageActivity,
                "Network error: projectId is null in the BacklogPageActivity",
                Toast.LENGTH_LONG
            ).show()
        }

        viewModel.tasksResponse.observe(this) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }

                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    val recyclerView: RecyclerView = findViewById(R.id.recyclerview)
                    recyclerView.layoutManager = LinearLayoutManager(this)
                    recyclerView.adapter = BacklogRecyclerViewAdapter(apiResponse.data)
                }

                is ApiResponse.Failure -> {
                    loadingIndicator.visibility = View.GONE
                    Toast.makeText(
                        this@BacklogPageActivity,
                        "Network error: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
