package com.example.taskermobile.activities.project

import android.content.Intent
import com.example.taskermobile.viewadapters.ProjectAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.R
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.eventlisteners.OnItemClickListener
import com.example.taskermobile.viewmodels.ProjectsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProjectsPageFragment : Fragment() {

    private val viewModel: ProjectsViewModel by viewModel()
    private lateinit var loadingIndicator: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.projects_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        viewModel.getAll()

//        val createProjectButton: Button = view.findViewById(R.id.buttonCreate)

//        createProjectButton.setOnClickListener {
//            val intent = Intent(requireActivity(), ProjectCreateFragment::class.java)
//            startActivity(intent)
//        }

        viewModel.projectsResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }

                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    val recyclerView: RecyclerView = view.findViewById(R.id.recyclerview)
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    recyclerView.adapter = ProjectAdapter(apiResponse.data, object :
                        OnItemClickListener {
                        override fun onItemClick(id: String) {
                            val bundle = bundleOf("PROJECT_ID" to id)
                            findNavController().navigate(R.id.action_projectsPageFragment_to_projectDetailFragment,
                                bundle)
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
    }
}

