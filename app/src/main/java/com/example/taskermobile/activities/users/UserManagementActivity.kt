package com.example.taskermobile.activities.users

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.MainActivity
import com.example.taskermobile.R
import com.example.taskermobile.model.user.UserModel
import com.example.taskermobile.model.user.UserUpdateModel
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.getIdFromToken
import com.example.taskermobile.utils.observeOnce
import com.example.taskermobile.viewadapters.UserAdapter
import com.example.taskermobile.viewmodels.ProjectsViewModel
import com.example.taskermobile.viewmodels.TokenViewModel
import com.example.taskermobile.viewmodels.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserManagementActivity : AppCompatActivity(), UserAdapter.OnUserActionListener {

    private lateinit var loadingIndicator: ProgressBar

    private val viewModel: UserViewModel by viewModel()
    private val projectViewModel: ProjectsViewModel by viewModel()
    private val tokenViewModel: TokenViewModel by viewModel()

    private lateinit var adapter: UserAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var currentUser: String

    private var projectId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.user_management_page)
        loadingIndicator = findViewById(R.id.loadingIndicator)
        val addUserButton : Button = findViewById(R.id.addUserButton)

        projectId = intent.getStringExtra("PROJECT_ID")
            ?: throw IllegalArgumentException("Project ID is required")

        recyclerView = findViewById(R.id.usersRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UserAdapter(mutableListOf(), projectId ?: "")
        adapter.listener = this
        recyclerView.adapter = adapter

        tokenViewModel.token.observe(this) { tokenValue ->
            if (tokenValue != null) {
                currentUser = getIdFromToken(tokenValue.token).toString()
            }
        }

        fetchAllData()

        addUserButton.setOnClickListener {
            val editText = EditText(this)
            AlertDialog.Builder(this)
                .setTitle("Add New User")
                .setView(editText)
                .setPositiveButton("Add") { dialog, which ->
                    val userName = editText.text.toString()
                    viewModel.updateUserProjects(UserUpdateModel(userName,
                        listOf(projectId!!),
                        null))
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun fetchAllData() {
        projectViewModel.getById(projectId!!)
        projectViewModel.projectGetByIdResponse.observe(this) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> loadingIndicator.visibility = View.VISIBLE
                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    val project = apiResponse.data

                    val allUsers = (project?.assignedUsers ?: emptyList()) + (project?.adminProjects ?: emptyList())
                    val adminUsers = project?.adminProjects ?: emptyList()

                    setupUsers(allUsers, adminUsers)
                }
                is ApiResponse.Failure -> {
                    loadingIndicator.visibility = View.GONE
                    Toast.makeText(this, "Failed to fetch project: ${apiResponse.errorMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onUserLongClick(userModel: UserModel, view: View, projectId: String) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.user_options_menu, popup.menu)

        popup.menu.findItem(R.id.remove_from_assign).isVisible = true
        popup.menu.findItem(R.id.remove_from_admin).isVisible = userModel.isAdmin
        popup.menu.findItem(R.id.promote_to_admin).isVisible = !userModel.isAdmin

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.remove_from_assign -> {
                    if (userModel.underControlProjects?.contains(projectId) == true &&
                        userModel.assignedProjects?.contains(projectId) == true) {
                        viewModel.updateUserProjects(UserUpdateModel(userModel.title, listOf(projectId), listOf(projectId)))
                    }
                    if (userModel.underControlProjects?.contains(projectId) == true) {
                        viewModel.updateUserProjects(UserUpdateModel(userModel.title, null, listOf(projectId)))
                    } else if (userModel.assignedProjects?.contains(projectId) == true) {
                        viewModel.updateUserProjects(UserUpdateModel(userModel.title, listOf(projectId), null))
                    } else {
                        viewModel.updateUserProjects(UserUpdateModel(userModel.title, null, null))
                    }
                }
                R.id.remove_from_admin -> {
                    userModel.underControlProjects = userModel.underControlProjects?.filter { it == projectId }
                    viewModel.updateUserProjects(UserUpdateModel(userModel.title, null, userModel.underControlProjects))
                }
                R.id.promote_to_admin -> {
                    viewModel.updateUserProjects(UserUpdateModel(userModel.title, null, listOf(projectId)))
                }
            }

            observeUserUpdateResponse(userModel.id, projectId)
            true
        }

        popup.show()
    }

    private fun observeUserUpdateResponse(userId: String, projectId: String) {
        viewModel.userUpdateResponse.observeOnce(this) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Success -> {
                    val updatedUser = apiResponse.data
                    if (updatedUser != null) {
                        if (isUserRemovedFromProject(updatedUser, projectId)) {
                            navigateToMainActivity()
                        } else {
                            fetchAllData()
                        }
                    }
                }
                is ApiResponse.Failure -> {
                    Toast.makeText(this,
                        "Failed to update user: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG).show()
                }
                is ApiResponse.Loading -> {
                }
            }
        }
    }

    private fun isUserRemovedFromProject(user: UserModel, projectId: String): Boolean {
        if (user.id != currentUser) {
            return false;
        }

        val isNotAssigned = user.assignedProjects?.none { it == projectId } ?: true
        val isNotAdmin = user.underControlProjects?.none { it == projectId } ?: true

        return isNotAssigned && isNotAdmin
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this@UserManagementActivity, MainActivity::class.java))
        finish()
    }

    @SuppressLint("NotifyDataSetChanged")
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
                    Toast.makeText(this,
                        "Failed to fetch users: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}