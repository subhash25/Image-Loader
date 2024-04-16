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
import kotlinx.coroutines.launch

class ListAdapter(private val dataList: List<ImageDataModelItem>,private val context: Context) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    private val imageLoader = EfficientImageLoader(context)
    // ViewHolder class to hold references to views
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageview)
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
        val imageUrl =
            "${imageThumbnail.domain}/${imageThumbnail.basePath}/0/${imageThumbnail.key}"
        //holder.imageView.setImageBitmap(null)
        CoroutineScope(Dispatchers.Main).launch {
            val bitmap = imageLoader.loadImage(imageUrl)
            bitmap?.let {
                holder.imageView.setImageBitmap(it)
            } ?: run {
                // Set placeholder image or handle error
                holder.imageView.setImageResource(R.drawable.placeholder)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}