package yawza.zawya.controller

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import yawza.zawya.R
import yawza.zawya.manager.MapManager
import yawza.zawya.repository.ZoneRepository
import yawza.zawya.viewmodel.MapViewModel

class MapController(
    private val context: Context,
    private val viewModel: MapViewModel,
    private val mapManager: MapManager,
    private val zoneRepository: ZoneRepository,
    private val onMapClick: (LatLng) -> Unit
) : OnMapReadyCallback {
    
    private val lausanneZones by lazy { zoneRepository.getLausanneZones() }
    
    fun setupMap(supportFragmentManager: androidx.fragment.app.FragmentManager) {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as? SupportMapFragment
        
        mapFragment?.getMapAsync(this)
    }
    
        override fun onMapReady(googleMap: GoogleMap) {
            mapManager.initializeMap(googleMap)
            mapManager.setMapClickListener { latLng -> onMapClick(latLng) }

            // Wait for actual user location instead of hardcoded
            // The location will be set by MainActivity when GPS location is available
        }
    
    fun handleZoomIn() {
        mapManager.zoomIn()
    }
    
    fun handleZoomOut() {
        mapManager.zoomOut()
    }
    
    fun handleRotateLeft() {
        val currentBearing = mapManager.getCurrentBearing()
        val newBearing = (currentBearing - 45f) % 360f
        mapManager.rotateAroundUser(viewModel.userLocation.value ?: return, newBearing)
    }
    
    fun handleRotateRight() {
        val currentBearing = mapManager.getCurrentBearing()
        val newBearing = (currentBearing + 45f) % 360f
        mapManager.rotateAroundUser(viewModel.userLocation.value ?: return, newBearing)
    }
    
    fun updateZonesWithUserLocation(userLocation: LatLng?) {
        mapManager.updateZoneOverlays(lausanneZones, userLocation)
    }
    
    fun onMapReady() {
        // This method can be called after map is ready to trigger location updates
        // The actual location getting will be handled by MainActivity
    }
}
