package yawza.zawya.utils

import android.content.Context
import android.graphics.Color
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import yawza.zawya.models.StickerZone

class ZoneOverlayManager(private val context: Context) {
    
    private val zoneCircles = mutableMapOf<String, Circle>()
    
    fun addZonesToMap(map: GoogleMap, zones: List<StickerZone>) {
        android.util.Log.d("ZoneOverlayManager", "Adding ${zones.size} zones to map")
        zones.forEach { zone ->
            addZoneToMap(map, zone)
        }
    }
    
    fun addZoneToMap(map: GoogleMap, zone: StickerZone) {
        android.util.Log.d("ZoneOverlayManager", "Adding zone: ${zone.brandName} at ${zone.center.latitude}, ${zone.center.longitude} with radius ${zone.radius}")
        
        val isVeryClose = zone.radius <= 20.0 // Very precise zone
        
        val circleOptions = CircleOptions()
            .center(zone.center)
            .radius(zone.radius)
            .fillColor(getZoneFillColor(zone, isVeryClose))
            .strokeColor(getZoneStrokeColor(zone, isVeryClose))
            .strokeWidth(if (isVeryClose) 5f else 3f) // Thicker border when very close
            .clickable(true)
        
        val circle = map.addCircle(circleOptions)
        zoneCircles[zone.id] = circle
        
        android.util.Log.d("ZoneOverlayManager", "Circle added successfully for zone ${zone.id}")
    }
    
    fun removeZone(zoneId: String) {
        zoneCircles[zoneId]?.remove()
        zoneCircles.remove(zoneId)
    }
    
    fun updateZone(zone: StickerZone) {
        removeZone(zone.id)
        // Note: You'll need to pass the map reference to update
        // This is a simplified version
    }
    
    fun clearAllZones() {
        zoneCircles.values.forEach { it.remove() }
        zoneCircles.clear()
    }
    
    private fun getZoneFillColor(zone: StickerZone, isVeryClose: Boolean = false): Int {
        val baseColor = when (zone.stickerCount) {
            in 0..5 -> Color.argb(100, Color.red(zone.color), Color.green(zone.color), Color.blue(zone.color))
            in 6..15 -> Color.argb(150, Color.red(zone.color), Color.green(zone.color), Color.blue(zone.color))
            else -> Color.argb(200, Color.red(zone.color), Color.green(zone.color), Color.blue(zone.color))
        }
        
        // Make very close zones more opaque and brighter
        return if (isVeryClose) {
            Color.argb(220, Color.red(zone.color), Color.green(zone.color), Color.blue(zone.color))
        } else {
            baseColor
        }
    }
    
    private fun getZoneStrokeColor(zone: StickerZone, isVeryClose: Boolean = false): Int {
        val baseColor = when (zone.stickerCount) {
            in 0..5 -> Color.argb(200, Color.red(zone.color), Color.green(zone.color), Color.blue(zone.color))
            in 6..15 -> Color.argb(255, Color.red(zone.color), Color.green(zone.color), Color.blue(zone.color))
            else -> Color.argb(255, Color.red(zone.color), Color.green(zone.color), Color.blue(zone.color))
        }
        
        // Make very close zones have a bright, pulsing effect
        return if (isVeryClose) {
            Color.argb(255, Color.red(zone.color), Color.green(zone.color), Color.blue(zone.color))
        } else {
            baseColor
        }
    }
    
    fun getZoneAt(latLng: LatLng, zones: List<StickerZone>): StickerZone? {
        return zones.find { zone ->
            val distance = FloatArray(1)
            android.location.Location.distanceBetween(
                latLng.latitude, latLng.longitude,
                zone.center.latitude, zone.center.longitude,
                distance
            )
            distance[0] <= zone.radius
        }
    }
}
