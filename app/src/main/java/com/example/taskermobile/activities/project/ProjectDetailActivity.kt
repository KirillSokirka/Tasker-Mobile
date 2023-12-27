package com.example.taskermobile.activities.project

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskermobile.LoginActivity
import com.example.taskermobile.MainActivity
import com.example.taskermobile.R
import com.example.taskermobile.activities.kanbanboard.KanbanBoardDetailActivity
import com.example.taskermobile.activities.release.ReleasesPageActivity
import com.example.taskermobile.activities.users.UserListActivity
import com.example.taskermobile.activities.users.UserManagementActivity
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.getIdFromToken
import com.example.taskermobile.viewmodels.ProjectsViewModel
import com.example.taskermobile.viewmodels.TokenViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProjectDetailActivity : AppCompatActivity() {

    private val viewModel: ProjectsViewModel by viewModel()
    private val tokenModel: TokenViewModel by viewModel()

    private lateinit var loadingIndicator: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val projectId = intent.getStringExtra("PROJECT_ID").toString()

        setContentView(R.layout.project_detail)

        loadingIndicator = findViewById(R.id.loadingIndicator)

        val title: TextView = findViewById(R.id.title)

        val editProjectButton : Button = findViewById(R.id.editProject)

        editProjectButton.setOnClickListener {
            val intent =
                Intent(this@ProjectDetailActivity, ProjectUpdateActivity::class.java)
            intent.putExtra("PROJECT_ID", projectId)
            startActivity(intent)
        }

        tokenModel.token.observe(this) { tokenValue ->
            if (tokenValue != null) {
                viewModel.getById(projectId)
            } else {
                loadingIndicator.visibility = View.VISIBLE
            }
        }

        val releasesButton : Button = findViewById(R.id.releasesInfo)
        val manageUsersButton: Button = findViewById(R.id.manageUsersButton)

        viewModel.projectGetByIdResponse.observe(this) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }

                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    apiResponse.data?.let { project ->

                        val token = tokenModel.token.value

                            val userId = getIdFromToken(token!!.token)

                            val users = (project.assignedUsers ?: emptyList()) + (project.adminProjects ?: emptyList())

                            if (!users.contains(userId)) {
                                startActivity(Intent(this@ProjectDetailActivity, MainActivity::class.java))
                            }


                        title.setText(project.title)

                        releasesButton.setOnClickListener{
                            val intent =
                                Intent(this@ProjectDetailActivity,
                                    ReleasesPageActivity::class.java)
                            intent.putExtra("PROJECT_ID", projectId)
                            startActivity(intent)
                            finish()
                        }

                        val spinner: Spinner = findViewById(R.id.kanbanBoardsSpinner)
                        val defaultTitle = "Select a Kanban Board"
                        val kanbanBoardNames = mutableListOf(defaultTitle)
                        project.kanbanBoards?.map { kanbanBoardNames.add(it.title.toString()) }
                        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, kanbanBoardNames)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner.adapter = adapter

                        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                                if (position == 0) {
                                    spinner.setSelection(0)
                                } else {
                                    val selectedBoardId = project.kanbanBoards?.get(position-1)?.id

                                    val intent = Intent(this@ProjectDetailActivity, KanbanBoardDetailActivity::class.java)
                                    intent.putExtra("KANBAN_BOARD_ID", selectedBoardId)
                                    startActivity(intent)
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                spinner.setSelection(0)
                            }
                        }

                        manageUsersButton.setOnClickListener {
                            val destinationClass = if (project.adminProjects?.contains(userId) == true) {
                                UserManagementActivity::class.java
                            } else {
                                UserListActivity::class.java
                            }

                            val intent = Intent(this@ProjectDetailActivity, destinationClass)
                            intent.putExtra("PROJECT_ID", projectId)
                            startActivity(intent)
                        }
                    }
                }

                is ApiResponse.Failure -> {
                    loadingIndicator.visibility = View.GONE
                    Toast.makeText(
                        this@ProjectDetailActivity,
                        "Network error: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}