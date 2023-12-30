package com.example.taskermobile.activities.release

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.R
import com.example.taskermobile.model.task.TaskPreviewModel
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.getIdFromToken
import com.example.taskermobile.viewadapters.TaskPreviewAdapter
import com.example.taskermobile.viewadapters.TasksAdapter
import com.example.taskermobile.viewmodels.ReleasesPageViewModel
import com.example.taskermobile.viewmodels.TaskViewModel
import com.example.taskermobile.viewmodels.TokenViewModel
import com.example.taskermobile.viewmodels.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class ReleaseDetailFragment : Fragment() {
    private val releaseModel by viewModel<ReleasesPageViewModel>()
    private val tokenModel by viewModel<TokenViewModel>()
    private val userModel by viewModel<UserViewModel>()

    private lateinit var loadingIndicator: ProgressBar
    private lateinit var userAdminProject: List<String>
    private lateinit var deleteButton: Button
    private lateinit var updateButton: Button
    private lateinit var releaseName: TextView
    private lateinit var releaseStatus: TextView
    private lateinit var endDate: TextView
    private lateinit var withoutTasks: TextView
    private lateinit var userId: String
    private lateinit var projectId: String

    private lateinit var tasksRecyclerView: RecyclerView
    private var tasks: List<TaskPreviewModel> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.release_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val releaseId = arguments?.getString("RELEASE_ID")
            ?: throw Exception("Missing release id")

        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        releaseName = view.findViewById(R.id.releaseName)
        releaseStatus = view.findViewById(R.id.releaseStatus)
        endDate = view.findViewById(R.id.endDate)
        withoutTasks = view.findViewById(R.id.withoutTasks)
        deleteButton = view.findViewById(R.id.deleteRelease)
        updateButton = view.findViewById(R.id.updateRelease)

        userAdminProject = emptyList()
        projectId = ""
        userId = ""

        tasksRecyclerView = view.findViewById(R.id.recyclerview)

        setUpObservers(releaseId)
    }

    private fun setUpObservers(releaseId: String) {
        tokenModel.token.observe(viewLifecycleOwner) { tokenValue ->
            if (tokenValue != null) {
                releaseModel.get(releaseId)

                userId = getIdFromToken(tokenValue.token).toString()
            } else {
                loadingIndicator.visibility = View.VISIBLE
            }
        }

        releaseModel.releaseByIdResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> loadingIndicator.visibility = View.VISIBLE
                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.VISIBLE


                    val originalDateString = apiResponse.data!!.endDate
                    val originalFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val targetFormat = SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault())

                    try {
                        val date = originalFormat.parse(originalDateString)
                        date?.let {
                            val formattedDate = targetFormat.format(it)
                            endDate.text =  formattedDate
                        }
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }

                    releaseName.text = apiResponse.data!!.title
                    releaseStatus.text = if(apiResponse.data!!.isReleased) "Released" else "Unreleased"
                    releaseStatus.setBackgroundResource(
                        if(apiResponse.data!!.isReleased) R.drawable.rounded_green_highlight
                        else R.drawable.rounded_red_highlight
                    )
                    projectId = apiResponse.data.projectId!!

                    tasks = apiResponse.data.tasks
                    setupTasksRecyclerView()

                    userModel.get(userId)
                }

                is ApiResponse.Failure -> {
                    loadingIndicator.visibility = View.VISIBLE
                    Toast.makeText(
                        requireContext(),
                        "Error: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            userModel.userGetResponse.observe(viewLifecycleOwner) { apiResponse ->
                when (apiResponse) {
                    is ApiResponse.Loading -> {
                        loadingIndicator.visibility = View.VISIBLE
                    }
                    is ApiResponse.Success -> {
                        loadingIndicator.visibility = View.GONE

                        setUpListeners(releaseId, apiResponse.data?.underControlProjects ?: emptyList())
                    }
                    is ApiResponse.Failure -> {
                        loadingIndicator.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Something went wrong: ${apiResponse.errorMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            releaseModel.deleteReleaseResponse.observe(viewLifecycleOwner) { apiResponse ->
                when (apiResponse) {
                    is ApiResponse.Loading -> loadingIndicator.visibility = View.VISIBLE

                    is ApiResponse.Success -> {
                        loadingIndicator.visibility = View.VISIBLE
                        findNavController().navigate(R.id.releasesPageFragment)
                    }

                    is ApiResponse.Failure -> {
                        loadingIndicator.visibility = View.VISIBLE
                        Toast.makeText(
                            requireContext(),
                            "Error: ${apiResponse.errorMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun setupTasksRecyclerView() {
        if(tasks.size != 0) {
            val adapter = TaskPreviewAdapter(tasks)
            tasksRecyclerView.adapter = adapter
            tasksRecyclerView.layoutManager = LinearLayoutManager(context)
        }
        else {
            withoutTasks.visibility = View.VISIBLE
            withoutTasks.text = "Tasks have not been added yet"
        }

    }

    private fun setUpListeners(releaseId: String, allowedProjects: List<String>) {
        if (allowedProjects.contains(projectId)) {
            deleteButton.visibility = View.VISIBLE
            deleteButton.setOnClickListener {
                releaseModel.delete(releaseId)
            }

            updateButton.visibility = View.VISIBLE
            updateButton.setOnClickListener {
                findNavController().navigate(R.id.action_releaseDetailFragment_to_releaseUpdateFragment, bundleOf("RELEASE_ID" to releaseId, "PROJECT_ID" to projectId))
            }
        }
    }
}
