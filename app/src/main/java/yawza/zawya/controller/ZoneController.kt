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
import yawza.zawya.viewmodel.ProfileViewModel

class ZoneController(
    private val context: Context,
    private val viewModel: MapViewModel,
    private val mapManager: MapManager,
    private val locationManager: LocationManager,
    private val navigationManager: NavigationManager,
    private val zoneRepository: ZoneRepository,
    private val profileViewModel: ProfileViewModel
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
                // Start QR code scanning
                startQRCodeScanning(zone)
            } else {
                navigationManager.showDistanceToast(distance.toInt())
            }
        } else {
            navigationManager.showLocationUnavailableToast()
        }
    }
    
    @Suppress("DEPRECATION")
    private fun startQRCodeScanning(_zone: StickerZone) {
        val intent = android.content.Intent(context, yawza.zawya.activity.QRScannerActivity::class.java)
        if (context is androidx.fragment.app.FragmentActivity) {
            context.startActivityForResult(intent, QR_SCAN_REQUEST_CODE)
        }
    }
    
    fun handleQRScanResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        if (requestCode == QR_SCAN_REQUEST_CODE && resultCode == android.app.Activity.RESULT_OK) {
            val stickerId = data?.getStringExtra("sticker_id")
            val zoneId = data?.getStringExtra("zone_id")
            val brandName = data?.getStringExtra("brand_name")
            
            if (stickerId != null && zoneId != null && brandName != null) {
                processScannedSticker(stickerId, zoneId, brandName)
            } else {
                navigationManager.showErrorToast("Invalid QR code data")
            }
        }
    }
    
    private fun processScannedSticker(stickerId: String, zoneId: String, brandName: String) {
        // Check if user already has this sticker
        if (profileViewModel.hasSticker(stickerId)) {
            navigationManager.showAlreadyCollectedToast(
                yawza.zawya.models.StickerZone(
                    id = zoneId,
                    center = com.google.android.gms.maps.model.LatLng(0.0, 0.0),
                    radius = 0.0,
                    brandName = brandName,
                    stickerCount = 1,
                    color = android.graphics.Color.GRAY
                )
            )
            return
        }
        
        // Collect sticker in map viewmodel
        viewModel.collectSticker(zoneId, yawza.zawya.models.StickerZone(
            id = zoneId,
            center = com.google.android.gms.maps.model.LatLng(0.0, 0.0),
            radius = 0.0,
            brandName = brandName,
            stickerCount = 1,
            color = android.graphics.Color.GRAY
        ))
        
        // Mint sticker on blockchain via ProfileViewModel
        profileViewModel.collectSticker(zoneId, brandName, 1)
        
        navigationManager.showCollectionToast(
            yawza.zawya.models.StickerZone(
                id = zoneId,
                center = com.google.android.gms.maps.model.LatLng(0.0, 0.0),
                radius = 0.0,
                brandName = brandName,
                stickerCount = 1,
                color = android.graphics.Color.GRAY
            ),
            1,
            1
        )
    }
    
    companion object {
        const val QR_SCAN_REQUEST_CODE = 1001
    }
    
    private fun simulateStickerCollection(zone: StickerZone) {
        // Check if user already has this sticker
        if (profileViewModel.hasSticker(zone.id)) {
            navigationManager.showAlreadyCollectedToast(zone)
            return
        }
        
        // Check if zone has remaining stickers
        val remainingStickers = viewModel.getRemainingStickers(zone)
        if (remainingStickers > 0) {
            // Collect sticker in map viewmodel
            viewModel.collectSticker(zone.id, zone)
            
            // Mint sticker on blockchain via ProfileViewModel
            profileViewModel.collectSticker(zone.id, zone.brandName, zone.stickerCount)
            
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
