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

    fun updateLegend(progressData: Map<String, Pair<Int, Int>>) {
        val mcdonaldsProgress = progressData["McDonald's"] ?: Pair(0, 5)
        val nikeProgress = progressData["Nike"] ?: Pair(0, 10)
        val sephoraProgress = progressData["Sephora"] ?: Pair(0, 15)

        binding.textLegendContent.text = context.getString(
            R.string.legend_content,
            mcdonaldsProgress.first, mcdonaldsProgress.second,
            nikeProgress.first, nikeProgress.second,
            sephoraProgress.first, sephoraProgress.second
        )
    }
}
