package com.example.taskermobile.viewadapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.R
import com.example.taskermobile.model.release.ReleasePreviewModel

class ReleaseAdapter(private val items: List<ReleasePreviewModel>?) : RecyclerView.Adapter<ReleaseAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitle: TextView = itemView.findViewById(R.id.releaseName)
        val textViewStatus: TextView = itemView.findViewById(R.id.releaseStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.release_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = items?.get(position)

        if (currentItem != null) {
            holder.textViewTitle.text = currentItem.title
            holder.textViewStatus.text = if (currentItem.isReleased) "Released" else "Unreleased"
        }
    }
}