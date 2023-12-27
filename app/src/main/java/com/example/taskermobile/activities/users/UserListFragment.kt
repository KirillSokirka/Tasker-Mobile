package com.example.taskermobile.activities.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.R
import com.example.taskermobile.model.user.UserModel
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.viewadapters.UserAdapter
import com.example.taskermobile.viewmodels.ProjectsViewModel
import com.example.taskermobile.viewmodels.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserListFragment : Fragment() {

    private var loadingIndicator: ProgressBar? = null

    private val viewModel: UserViewModel by viewModel()
    private val projectViewModel: ProjectsViewModel by viewModel()

    private var adapter: UserAdapter? = null
    private var recyclerView: RecyclerView? = null

    private var projectId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            projectId = it.getString("PROJECT_ID")
        } ?: throw IllegalArgumentException("Project ID is required")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingIndicator = view.findViewById(R.id.loadingIndicator)

        recyclerView = view.findViewById(R.id.usersRecyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        adapter = UserAdapter(mutableListOf(), projectId ?: "")
        recyclerView?.adapter = adapter

        fetchAllData()
    }

    private fun fetchAllData() {
        projectId?.let { nonNullProjectId ->
            projectViewModel.getById(nonNullProjectId)
            projectViewModel.projectGetByIdResponse.observe(viewLifecycleOwner) { apiResponse ->
                when (apiResponse) {
                    is ApiResponse.Loading -> loadingIndicator?.visibility = View.VISIBLE
                    is ApiResponse.Success -> {
                        loadingIndicator?.visibility = View.GONE
                        val project = apiResponse.data

                        val allUsers = (project?.assignedUsers ?: emptyList()) + (project?.adminProjects
                            ?: emptyList())
                        val adminUsers = project?.adminProjects ?: emptyList()

                        setupUsers(allUsers, adminUsers)
                    }

                    is ApiResponse.Failure -> {
                        loadingIndicator?.visibility = View.GONE
                        Toast.makeText(
                            context,
                            "Failed to fetch project: ${apiResponse.errorMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun setupUsers(allUsers: List<String>, adminUsers: List<String>) {
        viewModel.getAll()
        viewModel.userResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator?.visibility = View.VISIBLE
                }
                is ApiResponse.Success -> {
                    loadingIndicator?.visibility = View.GONE
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

                    adapter?.setItems(projectUsers)
                    adapter?.notifyDataSetChanged()
                }
                is ApiResponse.Failure -> {
                    loadingIndicator?.visibility = View.GONE
                    Toast.makeText(
                        context,
                        "Failed to fetch users: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(projectId: String) =
            UserListFragment().apply {
                arguments = Bundle().apply {
                    putString("PROJECT_ID", projectId)
                }
            }
    }
}
