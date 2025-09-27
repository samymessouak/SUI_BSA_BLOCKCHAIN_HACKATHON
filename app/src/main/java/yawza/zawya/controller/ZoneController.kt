package yawza.zawya.controller

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import yawza.zawya.manager.LocationManager
import yawza.zawya.manager.MapManager
import yawza.zawya.manager.NavigationManager
import yawza.zawya.models.StickerZone
import yawza.zawya.repository.ZoneRepository
import yawza.zawya.utils.ZoneInfoDialog
import yawza.zawya.viewmodel.MapViewModel

class ZoneController(
    private val context: Context,
    private val viewModel: MapViewModel,
    private val mapManager: MapManager,
    private val locationManager: LocationManager,
    private val navigationManager: NavigationManager,
    private val zoneRepository: ZoneRepository
) {
    
    private val lausanneZones by lazy { zoneRepository.getLausanneZones() }
    
    fun handleMapClick(clickedLocation: LatLng): StickerZone? {
        return lausanneZones.find { zone ->
            val distance = locationManager.calculateDistance(clickedLocation, zone.center)
            distance <= zone.radius
        }
    }
    
    fun showZoneInfoDialog(zone: StickerZone) {
        val dialog = ZoneInfoDialog(context, zone) { action ->
            when (action) {
                "scan" -> handleStickerScan(zone)
                "navigate" -> navigateToZone(zone)
            }
        }
        dialog.show()
    }
    
    private fun handleStickerScan(zone: StickerZone) {
        val userLocation = viewModel.userLocation.value
        if (userLocation != null) {
            val distance = locationManager.calculateDistance(userLocation, zone.center)
            
            if (distance <= 50) {
                // TODO: Implement actual camera QR scanning
                simulateStickerCollection(zone)
            } else {
                navigationManager.showDistanceToast(distance.toInt())
            }
        } else {
            navigationManager.showLocationUnavailableToast()
        }
    }
    
    private fun simulateStickerCollection(zone: StickerZone) {
        val remainingStickers = viewModel.getRemainingStickers(zone)
        if (remainingStickers > 0) {
            viewModel.collectSticker(zone.id, zone)
            val collected = viewModel.collectedStickers.value?.get(zone.id) ?: 0
            navigationManager.showCollectionToast(zone, collected, zone.stickerCount)
        } else {
            navigationManager.showCompletionToast(zone)
        }
    }
    
    private fun navigateToZone(zone: StickerZone) {
        val userLocation = viewModel.userLocation.value
        if (userLocation != null) {
            mapManager.navigateToZone(zone, userLocation)
            
            // Return to user after 3 seconds
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                mapManager.returnToUserLocation(userLocation)
            }, 3000)
            
            val distance = locationManager.calculateDistance(userLocation, zone.center)
            navigationManager.showNavigationToast(zone, distance.toInt())
        }
    }
    
    fun navigateToBrandZones(brandName: String) {
        val availableZones = viewModel.getAvailableZonesForBrand(brandName, lausanneZones)
        
        if (availableZones.isNotEmpty()) {
            if (availableZones.size == 1) {
                navigateToZone(availableZones[0])
            } else {
                navigationManager.showZoneSelectionDialog(brandName, availableZones) { zone ->
                    navigateToZone(zone)
                }
            }
        } else {
            navigationManager.showAllCollectedToast(brandName)
        }
    }
    
    fun getProgressData(): Map<String, Pair<Int, Int>> {
        return mapOf(
            "McDonald's" to viewModel.getProgressForBrand("McDonald's", lausanneZones),
            "Nike" to viewModel.getProgressForBrand("Nike", lausanneZones),
            "Sephora" to viewModel.getProgressForBrand("Sephora", lausanneZones)
        )
    }
}
