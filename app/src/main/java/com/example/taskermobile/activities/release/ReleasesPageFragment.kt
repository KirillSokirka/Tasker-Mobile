package com.example.taskermobile.activities.release

import SharedPreferencesService
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.R
import com.example.taskermobile.model.release.ReleaseCreateModel
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.eventlisteners.OnItemClickListener
import com.example.taskermobile.viewadapters.ReleaseAdapter
import com.example.taskermobile.viewmodels.ReleasesPageViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReleasesPageFragment : Fragment() {
    private val sharedPreferencesService: SharedPreferencesService by inject()
    private val viewModel by viewModel<ReleasesPageViewModel>()
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var createRelease: Button
    private lateinit var titleEdit : EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.releases_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val projectId = sharedPreferencesService.retrieveData("lastProjectActive")

        if (projectId == null) {
            findNavController().navigate(R.id.action_releasesPageFragment_to_projectsPageFragment)
            return
        }

        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        recyclerView = view.findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        createRelease = view.findViewById(R.id.addReleaseButton)
        titleEdit = EditText(requireContext())

        viewModel.getAll(projectId)
        setUpObservers(projectId)
    }

    private fun setUpObservers(projectId: String) {
        viewModel.releasesResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }
                is ApiResponse.Success -> {
                    setUpListeners(projectId)
                    loadingIndicator.visibility = View.GONE
                    recyclerView.adapter = ReleaseAdapter(apiResponse.data, object :
                        OnItemClickListener {
                        override fun onItemClick(id: String) {
                            findNavController().navigate(
                                R.id.action_releasesPageFragment_to_releaseDetailFragment,
                                bundleOf("RELEASE_ID" to id)
                            )
                        }
                        })
                }
                is ApiResponse.Failure -> {
                    loadingIndicator.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Network error: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        viewModel.releaseCreateResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }
                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    findNavController().navigate(R.id.releasesPageFragment)
                }
                is ApiResponse.Failure -> {
                    loadingIndicator.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Network error: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun setUpListeners(projectId: String) {
        createRelease.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Create release")
                .setView(titleEdit)
                .setPositiveButton("Add") { dialog, which ->
                    val title = titleEdit.text.toString()
                    if (title.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "The project name cannot be empty",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        viewModel.create(ReleaseCreateModel(
                            title = title,
                            projectId = projectId))
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}
