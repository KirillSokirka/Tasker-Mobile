import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskermobile.R
import com.example.taskermobile.model.ProjectPreviewModel

class ProjectPreviewRecyclerViewAdapter(private val items: List<ProjectPreviewModel>?) : RecyclerView.Adapter<ProjectPreviewRecyclerViewAdapter.ViewHolder>() {

    // Create a ViewHolder to hold the views for each item
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        val textViewId: TextView = itemView.findViewById(R.id.textViewId)
        // Add more TextViews or views for other fields as needed
    }

    // Create the ViewHolder for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.project_preview, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    // Bind the data to the views
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = items?.get(position)

        if (currentItem != null) {
            holder.textViewTitle.text = currentItem.title
            holder.textViewId.text = currentItem.id
        }
    }
}