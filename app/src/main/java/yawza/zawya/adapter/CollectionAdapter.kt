package yawza.zawya.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import yawza.zawya.R
import yawza.zawya.models.CollectionItem

class CollectionAdapter(
    private val collections: List<CollectionItem>
) : RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder>() {

    class CollectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val brandIcon: ImageView = itemView.findViewById(R.id.image_brand_icon)
        val brandName: TextView = itemView.findViewById(R.id.text_brand_name)
        val progress: TextView = itemView.findViewById(R.id.text_progress)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
        val percentage: TextView = itemView.findViewById(R.id.text_percentage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collection, parent, false)
        return CollectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        val collection = collections[position]
        
        // Set brand icon (using emoji for now)
        holder.brandIcon.setImageResource(android.R.drawable.ic_menu_gallery) // Placeholder
        
        // Set brand name
        holder.brandName.text = "${collection.brandIcon} ${collection.brandName}"
        
        // Set progress text
        holder.progress.text = "${collection.collectedStickers}/${collection.totalStickers} stickers"
        
        // Set progress bar
        holder.progressBar.progress = collection.progressPercentage
        
        // Set percentage
        holder.percentage.text = "${collection.progressPercentage}%"
        
        // Set brand color
        holder.brandName.setTextColor(collection.brandColor)
        holder.percentage.setTextColor(collection.brandColor)
        
        // Change progress bar color based on brand
        holder.progressBar.progressTintList = android.content.res.ColorStateList.valueOf(collection.brandColor)
    }

    override fun getItemCount(): Int = collections.size
}
