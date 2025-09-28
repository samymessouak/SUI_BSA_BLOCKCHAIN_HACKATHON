package yawza.zawya.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import yawza.zawya.R
import yawza.zawya.models.StickerToken

class StickerTransferAdapter(
    private val stickers: List<StickerToken>,
    private val onSelectionChanged: (List<String>) -> Unit
) : RecyclerView.Adapter<StickerTransferAdapter.StickerViewHolder>() {

    private val selectedStickers = mutableSetOf<String>()
    private val ownedStickers = stickers.filter { it.metadata["owner"]?.isNotEmpty() == true }

    class StickerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stickerIcon: TextView = itemView.findViewById(R.id.text_sticker_icon)
        val stickerName: TextView = itemView.findViewById(R.id.text_sticker_name)
        val lockIcon: ImageView = itemView.findViewById(R.id.image_lock)
        val selectionOverlay: View = itemView.findViewById(R.id.view_selection_overlay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StickerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sticker_transfer, parent, false)
        return StickerViewHolder(view)
    }

    override fun onBindViewHolder(holder: StickerViewHolder, position: Int) {
        val sticker = stickers[position]
        val isOwned = ownedStickers.any { it.id == sticker.id }
        val isSelected = selectedStickers.contains(sticker.id)

        // Set sticker icon and name
        holder.stickerIcon.text = getBrandIcon(sticker.brandName)
        holder.stickerName.text = sticker.brandName

        // Set lock icon visibility
        holder.lockIcon.visibility = if (isOwned) View.GONE else View.VISIBLE

        // Set selection state
        holder.selectionOverlay.visibility = if (isSelected) View.VISIBLE else View.GONE

        // Set colors based on ownership
        if (isOwned) {
            holder.stickerIcon.setTextColor(Color.BLACK)
            holder.stickerName.setTextColor(Color.BLACK)
            holder.itemView.setBackgroundColor(Color.WHITE)
        } else {
            holder.stickerIcon.setTextColor(Color.GRAY)
            holder.stickerName.setTextColor(Color.GRAY)
            holder.itemView.setBackgroundColor(Color.LTGRAY)
        }

        // Set click listener
        holder.itemView.setOnClickListener {
            if (isOwned) {
                toggleSelection(sticker.id)
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int = stickers.size

    private fun toggleSelection(stickerId: String) {
        if (selectedStickers.contains(stickerId)) {
            selectedStickers.remove(stickerId)
        } else {
            selectedStickers.add(stickerId)
        }
        onSelectionChanged(selectedStickers.toList())
    }

    fun getSelectedStickers(): List<String> = selectedStickers.toList()

    fun clearSelection() {
        selectedStickers.clear()
        notifyDataSetChanged()
        onSelectionChanged(emptyList())
    }

    private fun getBrandIcon(brandName: String): String {
        return when (brandName) {
            "McDonald's" -> "🍔"
            "Nike" -> "👟"
            "Sephora" -> "💄"
            else -> "🏷️"
        }
    }
}
