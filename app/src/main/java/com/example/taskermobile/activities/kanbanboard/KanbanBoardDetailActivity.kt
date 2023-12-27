package com.example.taskermobile.activities.kanbanboard

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.R
import com.example.taskermobile.model.kanbanboard.KanbanBoardModel
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.viewadapters.KanbanBoardAdapter
import com.example.taskermobile.viewmodels.KanbanBoardViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class KanbanBoardDetailActivity : AppCompatActivity() {

    private lateinit var boardAdapter: KanbanBoardAdapter
    private lateinit var kanbanBoardRecyclerView: RecyclerView
    private val kanbanBoardViewModel: KanbanBoardViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kanbanboard)

        kanbanBoardRecyclerView = findViewById(R.id.kanbanBoardRecyclerView)
        kanbanBoardRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val boardId = intent.getStringExtra("KANBAN_BOARD_ID")
        fetchKanbanBoard(boardId!!)
    }

    private fun fetchKanbanBoard(boardId: String ): KanbanBoardModel? {
        var result: KanbanBoardModel? = null

        kanbanBoardViewModel.getById(boardId)
        kanbanBoardViewModel.kanbanBoardResponse.observe(this) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Success -> {
                    apiResponse.data?.let { board ->
                        boardAdapter = KanbanBoardAdapter(board.columns ?: listOf())
                        kanbanBoardRecyclerView.adapter = boardAdapter
                    }
                }
                is ApiResponse.Failure -> {
                    Toast.makeText(
                        this,
                        "Failed to fetch users: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> return@observe
            }
        }

        return result
    }
}
