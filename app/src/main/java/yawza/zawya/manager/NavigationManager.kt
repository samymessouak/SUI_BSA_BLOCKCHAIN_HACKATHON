package yawza.zawya.manager

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import yawza.zawya.models.StickerZone

class NavigationManager(private val context: Context) {
    
    fun showZoneSelectionDialog(
        brandName: String, 
        zones: List<StickerZone>,
        onZoneSelected: (StickerZone) -> Unit
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Select ${brandName} Zone")
        
        val zoneNames = zones.mapIndexed { index, zone ->
            "${index + 1}. ${zone.id} - ${zone.stickerCount} stickers left"
        }.toTypedArray()
        
        builder.setItems(zoneNames) { _, which ->
            onZoneSelected(zones[which])
        }
        
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
    
    fun showNavigationToast(zone: StickerZone, distance: Int) {
        Toast.makeText(
            context, 
            "Showing ${zone.brandName} zone: ${zone.stickerCount} stickers left (${distance}m away)", 
            Toast.LENGTH_LONG
        ).show()
    }
    
    fun showCollectionToast(zone: StickerZone, collected: Int, total: Int) {
        Toast.makeText(
            context,
            "Sticker collected! $collected/$total from ${zone.brandName}",
            Toast.LENGTH_SHORT
        ).show()
    }
    
    fun showCompletionToast(zone: StickerZone) {
        Toast.makeText(
            context,
            "All stickers collected from this zone!",
            Toast.LENGTH_SHORT
        ).show()
    }
    
    fun showDistanceToast(distance: Int) {
        Toast.makeText(
            context,
            "You need to be closer to scan stickers (within 50m). Current distance: ${distance}m",
            Toast.LENGTH_LONG
        ).show()
    }
    
    fun showLocationUnavailableToast() {
        Toast.makeText(
            context,
            "Location not available",
            Toast.LENGTH_SHORT
        ).show()
    }
    
    fun showAllCollectedToast(brandName: String) {
        Toast.makeText(
            context,
            "All ${brandName} stickers have been collected!",
            Toast.LENGTH_SHORT
        ).show()
    }
    
    fun showAlreadyCollectedToast(zone: StickerZone) {
        Toast.makeText(
            context,
            "You already have this ${zone.brandName} sticker!",
            Toast.LENGTH_SHORT
        ).show()
    }
}
