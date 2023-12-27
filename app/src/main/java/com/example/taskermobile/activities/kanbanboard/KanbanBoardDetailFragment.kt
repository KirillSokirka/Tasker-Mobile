package com.example.taskermobile.activities.kanbanboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.R
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.viewadapters.KanbanBoardAdapter
import com.example.taskermobile.viewmodels.KanbanBoardViewModel

class KanbanBoardDetailFragment : Fragment() {

    private lateinit var boardAdapter: KanbanBoardAdapter
    private lateinit var kanbanBoardRecyclerView: RecyclerView
    private val kanbanBoardViewModel: KanbanBoardViewModel by viewModels()

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

        val boardId = arguments?.getString("KANBAN_BOARD_ID")
            ?: throw IllegalArgumentException("Board ID is required")

        kanbanBoardViewModel.getById(boardId)
        kanbanBoardViewModel.kanbanBoardResponse.observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Success -> {
                    apiResponse.data?.let { board ->
                        boardAdapter = KanbanBoardAdapter(board.columns ?: listOf())
                        kanbanBoardRecyclerView.adapter = boardAdapter
                    }
                }
                is ApiResponse.Failure -> {
                    Toast.makeText(
                        requireContext(),
                        "Failed to fetch users: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> { }
            }
        }
    }
}
