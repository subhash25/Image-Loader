package assignment.imageloader

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ListAdapter(private val dataList: List<ImageDataModelItem>, context: Context) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    private val imageLoader = EfficientImageLoader(context)
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageview)
        var currentImageUrl = ""
        var currentJob: Job? = null
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_view, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageThumbnail = dataList[position].thumbnail
        holder.currentImageUrl =
            "${imageThumbnail.domain}/${imageThumbnail.basePath}/0/${imageThumbnail.key}"
        holder.imageView.setImageResource(R.drawable.placeholder)
        val job = CoroutineScope(Dispatchers.Main).launch {
            val bitmap = imageLoader.loadImage(holder.currentImageUrl)
            bitmap?.let {
                holder.imageView.setImageBitmap(it)
            } ?: run {
                holder.imageView.setImageResource(R.drawable.picture_loading_failed)
            }
        }
        holder.currentJob = job
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.currentJob?.cancel()
    }
}