import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.R
import com.example.taskermobile.model.task.TaskPreviewModel
import com.example.taskermobile.utils.eventlisteners.OnItemClickListener

class BacklogAdapter(private val items: List<TaskPreviewModel>,
                     private val listener: OnItemClickListener
    ) : RecyclerView.Adapter<BacklogAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    items[position].id.let { id ->
                        listener.onItemClick(id)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_preview, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = items?.get(position)

        if (currentItem != null) {
            holder.textViewTitle.text = currentItem.title
            holder.textViewTitle.visibility = View.VISIBLE
            holder.textViewDescription.text = currentItem.description
            holder.textViewDescription.visibility = View.VISIBLE
        }


    }
}