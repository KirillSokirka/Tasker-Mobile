package com.example.taskermobile.activities.users

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.R
import com.example.taskermobile.model.user.UserModel
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.viewadapters.UserAdapter
import com.example.taskermobile.viewmodels.ProjectsViewModel
import com.example.taskermobile.viewmodels.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserListActivity : AppCompatActivity() {

    private lateinit var loadingIndicator: ProgressBar

    private val viewModel: UserViewModel by viewModel()
    private val projectViewModel: ProjectsViewModel by viewModel()

    private lateinit var adapter: UserAdapter
    private lateinit var recyclerView: RecyclerView

    private var projectId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.user_list_page)
        loadingIndicator = findViewById(R.id.loadingIndicator)

        projectId = intent.getStringExtra("PROJECT_ID")
            ?: throw IllegalArgumentException("Project ID is required")

        recyclerView = findViewById(R.id.usersRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UserAdapter(mutableListOf(), projectId ?: "")
        recyclerView.adapter = adapter

        fetchAllData()
    }

    private fun fetchAllData() {
        projectViewModel.getById(projectId!!)
        projectViewModel.projectGetByIdResponse.observe(this) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> loadingIndicator.visibility = View.VISIBLE
                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    val project = apiResponse.data

                    val allUsers = (project?.assignedUsers ?: emptyList()) + (project?.adminProjects
                        ?: emptyList())
                    val adminUsers = project?.adminProjects ?: emptyList()

                    setupUsers(allUsers, adminUsers)
                }

                is ApiResponse.Failure -> {
                    loadingIndicator.visibility = View.GONE
                    Toast.makeText(
                        this,
                        "Failed to fetch project: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun setupUsers(allUsers: List<String>, adminUsers: List<String>) {
        viewModel.getAll()
        viewModel.userResponse.observe(this) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }
                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    val users = apiResponse.data ?: emptyList()

                    val projectUsers: List<UserModel> = users
                        .filter { user -> user.id in allUsers }
                        .map { user ->
                            UserModel(
                                id = user.id,
                                title = user.title ?: "",
                                isAdmin = user.id in adminUsers,
                                assignedProjects = user.assignedProjects,
                                underControlProjects = user.underControlProjects
                            )
                        }

                    adapter.setItems(projectUsers)
                    adapter.notifyDataSetChanged()
                }
                is ApiResponse.Failure -> {
                    loadingIndicator.visibility = View.GONE
                    Toast.makeText(
                        this,
                        "Failed to fetch users: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}

