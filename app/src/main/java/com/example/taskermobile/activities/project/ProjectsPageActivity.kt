package com.example.taskermobile.activities.project

import android.content.Intent
import com.example.taskermobile.viewadapters.ProjectAdapter
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.R
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.eventlisteners.OnItemClickListener
import com.example.taskermobile.viewmodels.ProjectsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProjectsPageActivity : AppCompatActivity() {

    private val viewModel: ProjectsViewModel by viewModel()
    private lateinit var loadingIndicator: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.projects_list)
        loadingIndicator = findViewById(R.id.loadingIndicator)

        viewModel.getAll()

        val createProject: Button = findViewById(R.id.buttonCreate)

        createProject.setOnClickListener {
            startActivity(Intent(this@ProjectsPageActivity,
                ProjectCreateActivity::class.java))
        }

        viewModel.projectsResponse.observe(this) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }

                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    val recyclerView: RecyclerView = findViewById(R.id.recyclerview)
                    recyclerView.layoutManager = LinearLayoutManager(this)
                    recyclerView.adapter = ProjectAdapter(apiResponse.data, object :
                        OnItemClickListener  {
                        override fun onItemClick(id: String) {
                            val intent =
                                Intent(this@ProjectsPageActivity, ProjectDetailActivity::class.java)
                            intent.putExtra("PROJECT_ID", id)
                            startActivity(intent)
                        }
                    })
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
