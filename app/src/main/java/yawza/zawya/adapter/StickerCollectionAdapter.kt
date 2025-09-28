package yawza.zawya.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import yawza.zawya.R
import yawza.zawya.models.StickerCollectionItem

class StickerCollectionAdapter(
    private val stickers: List<StickerCollectionItem>
) : RecyclerView.Adapter<StickerCollectionAdapter.StickerViewHolder>() {

    class StickerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stickerIcon: TextView = itemView.findViewById(R.id.text_sticker_icon)
        val stickerName: TextView = itemView.findViewById(R.id.text_sticker_name)
        val lockIcon: ImageView = itemView.findViewById(R.id.image_lock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StickerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sticker_collection, parent, false)
        return StickerViewHolder(view)
    }

    override fun onBindViewHolder(holder: StickerViewHolder, position: Int) {
        val sticker = stickers[position]

        // Set sticker icon and name
        holder.stickerIcon.text = sticker.brandIcon
        holder.stickerName.text = sticker.name

        // Set lock icon visibility
        holder.lockIcon.visibility = if (sticker.isOwned) View.GONE else View.VISIBLE

        // Set colors based on ownership
        if (sticker.isOwned) {
            holder.stickerIcon.setTextColor(Color.BLACK)
            holder.stickerName.setTextColor(Color.BLACK)
            holder.itemView.setBackgroundColor(Color.WHITE)
        } else {
            holder.stickerIcon.setTextColor(Color.GRAY)
            holder.stickerName.setTextColor(Color.GRAY)
            holder.itemView.setBackgroundColor(Color.LTGRAY)
        }
    }

    override fun getItemCount(): Int = stickers.size
}
