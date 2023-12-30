package com.example.taskermobile.viewadapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.R
import com.example.taskermobile.model.taskstatus.TaskStatusBoardModel
import com.example.taskermobile.utils.eventlisteners.OnItemClickListener

class KanbanBoardAdapter(private val columns: List<TaskStatusBoardModel>,
                         private val listener: OnItemClickListener
) : RecyclerView.Adapter<KanbanBoardAdapter.ColumnViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColumnViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.column_item, parent, false)
        return ColumnViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColumnViewHolder, position: Int) {
        holder.bind(columns[position])
    }

    override fun getItemCount() = columns.size

    inner class ColumnViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tasksRecyclerView: RecyclerView = itemView.findViewById(R.id.tasksRecyclerView)
        private val columnNameTextView: TextView = itemView.findViewById(R.id.columnNameTextView)

        fun bind(column: TaskStatusBoardModel) {
            columnNameTextView.text = column.name
            tasksRecyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            tasksRecyclerView.adapter = TasksAdapter(column.tasks ?: listOf(), columns, listener)
        }
    }
}
