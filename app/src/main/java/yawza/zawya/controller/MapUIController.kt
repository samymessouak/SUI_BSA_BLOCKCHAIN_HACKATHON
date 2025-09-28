package yawza.zawya.controller

import android.content.Context
import android.widget.TextView
import androidx.cardview.widget.CardView
import yawza.zawya.R
import yawza.zawya.databinding.FragmentMapBinding
import yawza.zawya.viewmodel.MapViewModel

class MapUIController(
    private val context: Context,
    private val binding: FragmentMapBinding,
    private val viewModel: MapViewModel
) {

    fun setupBrandButtons(
        onMcDonaldsClick: () -> Unit,
        onNikeClick: () -> Unit,
        onSephoraClick: () -> Unit
    ) {
        binding.btnMcdonalds.setOnClickListener { onMcDonaldsClick() }
        binding.btnNike.setOnClickListener { onNikeClick() }
        binding.btnSephora.setOnClickListener { onSephoraClick() }
    }

    fun setupZoomControls(
        onZoomInClick: () -> Unit,
        onZoomOutClick: () -> Unit
    ) {
        binding.btnZoomIn.setOnClickListener { onZoomInClick() }
        binding.btnZoomOut.setOnClickListener { onZoomOutClick() }
    }

    fun setupRotationControls(
        onRotateLeftClick: () -> Unit,
        onRotateRightClick: () -> Unit
    ) {
        binding.btnRotateLeft.setOnClickListener { onRotateLeftClick() }
        binding.btnRotateRight.setOnClickListener { onRotateRightClick() }
    }

    fun updateLegend(_progressData: Map<String, Pair<Int, Int>>) {
        // Show only total stickers available, not collected count
        binding.textLegendContent.text = "🍔 McDonald's (2 stickers)\n👟 Nike (3 stickers)\n💄 Sephora (4 stickers)"
    }
    
    fun setupPersistentScanButton(onScanClick: () -> Unit) {
        binding.btnScanPersistent.setOnClickListener { onScanClick() }
    }
    
    fun updatePersistentScanButton(distance: Float?, nearestZoneBrand: String?) {
        val isCloseEnough = distance != null && distance <= 20.0f
        val isVeryClose = distance != null && distance <= 10.0f
        
        if (isCloseEnough) {
            // User is close enough to scan - full opacity
            binding.btnScanPersistent.isEnabled = true
            binding.btnScanPersistent.alpha = 1.0f
            
            if (isVeryClose) {
                binding.btnScanPersistent.text = "📱\nNOW!"
            } else {
                binding.btnScanPersistent.text = "📱\nSCAN"
            }
        } else {
            // User is too far to scan - reduced opacity (hollow effect)
            binding.btnScanPersistent.isEnabled = false
            binding.btnScanPersistent.alpha = 0.3f
            binding.btnScanPersistent.text = "📱\nSCAN"
        }
    }
}
