package assignment.imageloader

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ListAdapter(private val dataList: List<ImageDataModelItem>,private val context: Context) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    private val imageLoader = EfficientImageLoader(context)
    // Coroutine scope for managing image loading tasks
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    // ViewHolder class to hold references to views
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageview)
        var currentImageUrl = ""
        var currentJob: Job? = null
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_view, parent, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageThumbnail = dataList[position].thumbnail
        holder.currentImageUrl =
            "${imageThumbnail.domain}/${imageThumbnail.basePath}/0/${imageThumbnail.key}"
        holder.imageView.setImageResource(R.drawable.placeholder)
        // Cancel previous coroutine job if it exists
        holder.currentJob?.cancel()
        val job = coroutineScope.launch {
            val bitmap = imageLoader.loadImage(holder.currentImageUrl)
            bitmap?.let {
                holder.imageView.setImageBitmap(it)
            } ?: run {
                // Set placeholder image or handle error
                holder.imageView.setImageResource(R.drawable.picture_loading_failed)
            }
        }
        // Store the job in the ViewHolder
        holder.currentJob = job
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    // Cancel coroutine scope when adapter is destroyed
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        coroutineScope.cancel()
    }
}