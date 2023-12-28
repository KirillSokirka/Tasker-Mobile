package com.example.taskermobile.activities.kanbanboard

import SharedPreferencesService
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.R
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.viewadapters.KanbanBoardAdapter
import com.example.taskermobile.viewmodels.KanbanBoardViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class KanbanBoardDetailFragment : Fragment() {

    private lateinit var boardAdapter: KanbanBoardAdapter
    private lateinit var kanbanBoardRecyclerView: RecyclerView
    private val kanbanBoardViewModel by viewModel<KanbanBoardViewModel>()

    private lateinit var loadingIndicator: ProgressBar
    private val sharedPreferences: SharedPreferencesService by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.kanbanboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        kanbanBoardRecyclerView = view.findViewById(R.id.kanbanBoardRecyclerView)
        kanbanBoardRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val boardId = arguments?.getString("KANBAN_BOARD_ID")?:sharedPreferences.retrieveData("lastKanbanBoard")
        if ( boardId == null ) {
            findNavController().navigate(R.id.action_kanbanBoardDetailFragment_to_projectsPageFragment)
            return
        }
        sharedPreferences.saveData("lastKanbanBoard", boardId)

        kanbanBoardViewModel.getById(boardId)
        kanbanBoardViewModel.kanbanBoardResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }
                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    apiResponse.data?.let { board ->
                        boardAdapter = KanbanBoardAdapter(board.columns ?: listOf())
                        kanbanBoardRecyclerView.adapter = boardAdapter
                    }
                }
                is ApiResponse.Failure -> {
                    loadingIndicator.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Failed to kanban board users: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
