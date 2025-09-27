package yawza.zawya.viewmodel

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import yawza.zawya.models.StickerZone

class MapViewModel : ViewModel() {
    
    // User location
    private val _userLocation = MutableLiveData<LatLng?>()
    val userLocation: LiveData<LatLng?> = _userLocation
    
    // Collected stickers tracking
    private val _collectedStickers = MutableLiveData<MutableMap<String, Int>>()
    val collectedStickers: LiveData<MutableMap<String, Int>> = _collectedStickers
    
    // Current zoom level
    private val _currentZoom = MutableLiveData<Float>()
    val currentZoom: LiveData<Float> = _currentZoom
    
    // Available zones for navigation
    private val _availableZones = MutableLiveData<List<StickerZone>>()
    val availableZones: LiveData<List<StickerZone>> = _availableZones
    
    init {
        _collectedStickers.value = mutableMapOf()
        _currentZoom.value = 17f
    }
    
    fun updateUserLocation(location: LatLng) {
        _userLocation.value = location
    }
    
    fun collectSticker(zoneId: String, zone: StickerZone) {
        val current = _collectedStickers.value ?: mutableMapOf()
        val currentCollected = current[zoneId] ?: 0
        if (currentCollected < zone.stickerCount) {
            current[zoneId] = currentCollected + 1
            _collectedStickers.value = current
        }
    }
    
    fun getRemainingStickers(zone: StickerZone): Int {
        val collected = _collectedStickers.value?.get(zone.id) ?: 0
        return zone.stickerCount - collected
    }
    
    fun getDistanceToZone(zone: StickerZone): Float {
        val userLoc = _userLocation.value ?: return Float.MAX_VALUE
        val distance = FloatArray(1)
        Location.distanceBetween(
            userLoc.latitude, userLoc.longitude,
            zone.center.latitude, zone.center.longitude,
            distance
        )
        return distance[0]
    }
    
    fun updateZoomLevel(zoom: Float) {
        _currentZoom.value = zoom
    }
    
    fun getAvailableZonesForBrand(brandName: String, allZones: List<StickerZone>): List<StickerZone> {
        return allZones.filter { zone ->
            zone.brandName == brandName && getRemainingStickers(zone) > 0
        }
    }
    
    fun getProgressForBrand(brandName: String, allZones: List<StickerZone>): Pair<Int, Int> {
        val brandZones = allZones.filter { it.brandName == brandName }
        val collected = brandZones.sumOf { _collectedStickers.value?.get(it.id) ?: 0 }
        val total = brandZones.sumOf { it.stickerCount }
        return Pair(collected, total)
    }
}
