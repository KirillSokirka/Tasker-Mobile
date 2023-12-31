package com.example.taskermobile.viewadapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.R
import com.example.taskermobile.model.task.TaskPreviewModel
import com.example.taskermobile.utils.eventlisteners.OnItemClickListener

class TaskPreviewAdapter(private val tasks: List<TaskPreviewModel>,
                         private val listener: OnItemClickListener
) : RecyclerView.Adapter<TaskPreviewAdapter.TaskPreviewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskPreviewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_preview, parent, false)
        return TaskPreviewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskPreviewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount() = tasks.size

    inner class TaskPreviewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val status: TextView = itemView.findViewById(R.id.textViewStatus)
        private val title: TextView = itemView.findViewById(R.id.textViewTitle)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    tasks[position].id.let { id ->
                        listener.onItemClick(id)
                    }
                }
            }
        }

        fun bind(task: TaskPreviewModel) {
            title.text = task.title
            title.visibility = View.VISIBLE
            status.text = if(!task.taskStatusName.isEmpty()) task.taskStatusName else "-"
            status.visibility = View.VISIBLE
        }
    }
}