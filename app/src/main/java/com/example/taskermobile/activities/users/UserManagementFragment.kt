package com.example.taskermobile.activities.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.R
import com.example.taskermobile.model.user.UserModel
import com.example.taskermobile.model.user.UserUpdateModel
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.getIdFromToken
import com.example.taskermobile.viewadapters.UserAdapter
import com.example.taskermobile.viewmodels.ProjectsViewModel
import com.example.taskermobile.viewmodels.TokenViewModel
import com.example.taskermobile.viewmodels.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserManagementFragment : Fragment(), UserAdapter.OnUserActionListener {

    private lateinit var loadingIndicator: ProgressBar

    private val viewModel: UserViewModel by viewModel()
    private val projectViewModel: ProjectsViewModel by viewModel()
    private val tokenViewModel: TokenViewModel by viewModel()

    private lateinit var adapter: UserAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var currentUser: String

    private var projectId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_management_page, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        val addUserButton: Button = view.findViewById(R.id.addUserButton)

        projectId = arguments?.getString("PROJECT_ID")
            ?: throw IllegalArgumentException("Project ID is required")

        recyclerView = view.findViewById(R.id.usersRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = UserAdapter(mutableListOf(), projectId ?: "")
        adapter.listener = this
        recyclerView.adapter = adapter

        tokenViewModel.token.observe(viewLifecycleOwner) { tokenValue ->
            if (tokenValue != null) {
                currentUser = getIdFromToken(tokenValue.token).toString()
            }
        }

        fetchAllData()

        addUserButton.setOnClickListener {
            val editText = EditText(requireContext())

            if (editText.parent != null) {
                (editText.parent as ViewGroup).removeView(editText)
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Add New User")
                .setView(editText)
                .setPositiveButton("Add") { dialog, which ->
                    val userName = editText.text.toString()
                    viewModel.updateUserProjects(UserUpdateModel(userName,
                        listOf(projectId!!),
                        null))

                    observeUserUpdateResponse(projectId!!)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun fetchAllData() {
        projectViewModel.getById(projectId!!)
        projectViewModel.projectGetByIdResponse.observe(viewLifecycleOwner) { apiResponse ->
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
                    Toast.makeText(requireContext(),
                        "Failed to fetch project: ${apiResponse.errorMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupUsers(allUsers: List<String>, adminUsers: List<String>) {
        viewModel.getAll()
        viewModel.userResponse.observe(viewLifecycleOwner) { apiResponse ->
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
                }
                is ApiResponse.Failure -> {
                    loadingIndicator.visibility = View.GONE
                    Toast.makeText(requireContext(),
                        "Failed to fetch users: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onUserLongClick(userModel: UserModel, view: View, projectId: String) {
        val popup = PopupMenu(requireContext(), view)
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
                    viewModel.updateUserProjects(UserUpdateModel(userModel.title, null, listOf(projectId)))
                }
                R.id.promote_to_admin -> {
                    viewModel.updateUserProjects(UserUpdateModel(userModel.title, null, listOf(projectId)))
                }
            }

            observeUserUpdateResponse(projectId)
            true
        }

        popup.show()
    }

    private fun observeUserUpdateResponse(projectId: String) {
        viewModel.userUpdateResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Success -> {
                    val updatedUser = apiResponse.data
                    if (updatedUser != null) {
                        if (isUserRemovedFromProject(updatedUser, projectId)) {
                            findNavController().navigate(R.id.action_userManagementFragment_to_projectsPageFragment)
                        } else {
                            projectViewModel.getById(projectId)
                        }
                    }
                }
                is ApiResponse.Failure -> {
                    Toast.makeText(requireContext(),
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
}