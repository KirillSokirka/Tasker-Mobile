package com.example.taskermobile.activities.backlogpage

import BacklogRecyclerViewAdapter
import SharedPreferencesService
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.R
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.viewmodels.BacklogPageViewModel
import org.koin.android.ext.android.inject


class BacklogPageFragment : Fragment() {
    private val sharedPreferencesService: SharedPreferencesService by inject()
    private val viewModel: BacklogPageViewModel by viewModels()
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.backlog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var projectId = sharedPreferencesService.retrieveData("lastProjectActive")
        if ( projectId == null ) {
//            findNavController().navigate(R.id.,
//                bundleOf("PROJECT_ID" to id))
        }

        super.onViewCreated(view, savedInstanceState)

        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        recyclerView = view.findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.getAll(projectId!!)

        viewModel.tasksResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }

                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    recyclerView.adapter = BacklogRecyclerViewAdapter(apiResponse.data)
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
}
