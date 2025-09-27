package yawza.zawya.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import yawza.zawya.R
import yawza.zawya.models.StickerZone

class ZoneInfoDialog(
    private val context: Context,
    private val zone: StickerZone,
    private val onActionClick: (String) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_zone_info, null)
        setContentView(view)
        
        // Make dialog background transparent
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        
        // Set dialog size
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        
        setupViews(view)
    }
    
    private fun setupViews(view: View) {
        val brandNameText = view.findViewById<TextView>(R.id.text_brand_name)
        val stickerCountText = view.findViewById<TextView>(R.id.text_sticker_count)
        val descriptionText = view.findViewById<TextView>(R.id.text_description)
        val scanButton = view.findViewById<Button>(R.id.button_scan)
        val navigateButton = view.findViewById<Button>(R.id.button_navigate)
        val closeButton = view.findViewById<Button>(R.id.button_close)
        
        // Set zone information
        brandNameText.text = zone.brandName
        stickerCountText.text = "${zone.stickerCount} stickers available"
        descriptionText.text = zone.description.ifEmpty { "Tap to scan for stickers in this area!" }
        
        // Set button click listeners
        scanButton.setOnClickListener {
            onActionClick("scan")
            dismiss()
        }
        
        navigateButton.setOnClickListener {
            onActionClick("navigate")
            dismiss()
        }
        
        closeButton.setOnClickListener {
            dismiss()
        }
        
        // Style the scan button based on zone color
        scanButton.setBackgroundColor(zone.color)
        scanButton.setTextColor(Color.WHITE)
    }
}
