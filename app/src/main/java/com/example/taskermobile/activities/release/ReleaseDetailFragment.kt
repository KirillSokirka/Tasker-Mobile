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
import com.example.taskermobile.R
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.getIdFromToken
import com.example.taskermobile.viewmodels.ReleasesPageViewModel
import com.example.taskermobile.viewmodels.TokenViewModel
import com.example.taskermobile.viewmodels.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReleaseDetailFragment : Fragment() {
    private val releaseModel by viewModel<ReleasesPageViewModel>()
    private val tokenModel by viewModel<TokenViewModel>()
    private val userModel by viewModel<UserViewModel>()

    private lateinit var loadingIndicator: ProgressBar
    private lateinit var userAdminProject: List<String>
    private lateinit var deleteButton: Button
    private lateinit var updateButton: Button
    private lateinit var releaseName: TextView
    private lateinit var userId: String
    private lateinit var projectId: String

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
        deleteButton = view.findViewById(R.id.deleteRelease)
        updateButton = view.findViewById(R.id.updateRelease)

        userAdminProject = emptyList()
        projectId = ""
        userId = ""

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

                    releaseName.text = apiResponse.data!!.title
                    projectId = apiResponse.data.projectId!!

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
