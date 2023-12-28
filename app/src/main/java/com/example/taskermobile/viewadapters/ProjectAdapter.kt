package com.example.taskermobile.viewadapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.R
import com.example.taskermobile.model.project.ProjectPreviewModel
import com.example.taskermobile.utils.eventlisteners.OnItemClickListener

class ProjectAdapter(private val items: List<ProjectPreviewModel>?,
                     private val listener: OnItemClickListener
)
    : RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    items?.get(position)?.id?.let { id ->
                        listener.onItemClick(id)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.project_list_item,
            parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = items?.get(position)

        if (currentItem != null) {
            holder.textViewTitle.text = currentItem.title
        }
    }
}