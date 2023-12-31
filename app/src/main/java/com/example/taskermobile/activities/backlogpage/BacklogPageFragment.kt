package com.example.taskermobile.activities.backlogpage

import BacklogAdapter
import SharedPreferencesService
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.R
import com.example.taskermobile.model.task.TaskBoardPreviewModel
import com.example.taskermobile.model.taskstatus.TaskStatusBoardModel
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.eventlisteners.OnItemClickListener
import com.example.taskermobile.viewmodels.TaskViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class BacklogPageFragment : Fragment() {
    private val sharedPreferencesService: SharedPreferencesService by inject()
    private val viewModel by viewModel<TaskViewModel>()
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.backlog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val projectId = sharedPreferencesService.retrieveData("lastProjectActive")

        if ( projectId == null ) {
            findNavController().navigate(R.id.action_backlogPageFragment_to_projectsPageFragment)
        }

        super.onViewCreated(view, savedInstanceState)

        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        recyclerView = view.findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.getBacklog(projectId!!)
        setUpObservers()
    }

    private fun setUpObservers() {
        viewModel.backlogResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }

                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    recyclerView.adapter = BacklogAdapter(apiResponse.data ?: emptyList(), object :
                        OnItemClickListener {
                        override fun onItemClick(id: String) {
                            findNavController().navigate(
                                R.id.action_backlogPageFragment_to_taskBacklogEditFragment,
                                bundleOf("TASK_ID" to id)
                            )
                        }

                        override fun onItemLongClick(
                            task: TaskBoardPreviewModel,
                            allStatuses: List<TaskStatusBoardModel>,
                            view: View?) {}}
                    )
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
