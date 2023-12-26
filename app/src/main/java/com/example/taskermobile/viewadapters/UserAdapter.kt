package com.example.taskermobile.viewadapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.R
import com.example.taskermobile.model.user.UserModel

class UserAdapter(private var items: MutableList<UserModel>, private val projectId: String) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    interface OnUserActionListener {
        fun onUserLongClick(userModel: UserModel, view: View, projectId: String)
    }

    var listener: OnUserActionListener? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitle: TextView = itemView.findViewById(R.id.username)
        val textViewStatus: TextView = itemView.findViewById(R.id.statusName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_management_page_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = items.get(position)

        if (currentItem != null) {
            holder.textViewTitle.text = currentItem.title
            holder.textViewStatus.text = if (currentItem.isAdmin) "Admin" else "Assignee"

            holder.itemView.setOnLongClickListener { view ->
                listener?.onUserLongClick(currentItem, view, projectId)
                true
            }
        }
    }

    fun setItems(newItems: List<UserModel>) {
        this.items.clear()
        this.items.addAll(newItems)
        notifyDataSetChanged()
    }
}