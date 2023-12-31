package com.example.taskermobile.viewadapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.R
import com.example.taskermobile.model.TaskPriority
import com.example.taskermobile.model.task.TaskBoardPreviewModel
import com.example.taskermobile.model.task.TaskPreviewModel
import com.example.taskermobile.model.taskstatus.TaskStatusBoardModel
import com.example.taskermobile.model.taskstatus.TaskStatusModel
import com.example.taskermobile.utils.eventlisteners.OnItemClickListener

class TasksAdapter(private val tasks: List<TaskBoardPreviewModel>,
                   private val allStatuses: List<TaskStatusBoardModel>,
                   private val listener: OnItemClickListener
) : RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount() = tasks.size

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskNameTextView: TextView = itemView.findViewById(R.id.taskNameTextView)
        private val taskAssigneeBoard: TextView = itemView.findViewById(R.id.taskAssigneeBoard)
        private val taskPriorityBoard: TextView = itemView.findViewById(R.id.taskPriorityBoard)

        fun bind(task: TaskBoardPreviewModel) {
            taskNameTextView.text = task.title
            taskAssigneeBoard.text = task.assignee
            val priority = TaskPriority.fromInt(task.priority)
            taskPriorityBoard.text = priority.name
        }

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    tasks[position].id.let { id ->
                        listener.onItemClick(id)
                    }
                }
            }

            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemLongClick(tasks[position], allStatuses, itemView)
                    true
                } else {
                    false
                }
            }
        }
    }
}
