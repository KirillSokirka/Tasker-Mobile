package com.example.taskermobile.activities.kanbanboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.R
import com.example.taskermobile.model.kanbanboard.KanbanBoardModel
import com.example.taskermobile.viewadapters.KanbanBoardAdapter

class KanbanBoardDetailActivity : AppCompatActivity() {

    private lateinit var boardAdapter: KanbanBoardAdapter
    private lateinit var kanbanBoardRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kanbanboard_detail)

        kanbanBoardRecyclerView = findViewById(R.id.kanbanBoardRecyclerView)
        kanbanBoardRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val boardId = intent.getStringExtra("KANBAN_BOARD_ID")
        val kanbanBoard = fetchKanbanBoard(boardId)

        kanbanBoard?.let { board ->
            boardAdapter = KanbanBoardAdapter(board.columns ?: listOf())
            kanbanBoardRecyclerView.adapter = boardAdapter
        }
    }

    private fun fetchKanbanBoard(boardId: String?): KanbanBoardModel? {
        // Replace with actual logic to fetch the KanbanBoardModel
        return null
    }
}
