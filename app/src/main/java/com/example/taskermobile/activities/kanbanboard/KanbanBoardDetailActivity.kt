package com.example.taskermobile.activities.kanbanboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.taskermobile.R

class KanbanBoardDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kanbanboard_detail)

        val boardId = intent.getStringExtra("KANBAN_BOARD_ID")

    }
}