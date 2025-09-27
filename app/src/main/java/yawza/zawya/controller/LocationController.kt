package yawza.zawya.controller

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.model.LatLng
import yawza.zawya.manager.LocationManager
import yawza.zawya.manager.MapManager
import yawza.zawya.repository.ZoneRepository
import yawza.zawya.viewmodel.MapViewModel

class LocationController(
    private val activity: FragmentActivity,
    private val viewModel: MapViewModel,
    private val mapManager: MapManager,
    private val locationManager: LocationManager,
    private val zoneRepository: ZoneRepository
) {
    
    private val lausanneZones by lazy { zoneRepository.getLausanneZones() }
    
    fun requestLocationPermissions() {
        if (!locationManager.hasLocationPermission()) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }
    
    fun handlePermissionResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            }
        }
    }
    
    fun getCurrentLocation() {
        // Hardcoded location: Lausanne Flon McDonald's
        val hardcodedLocation = LatLng(46.5250, 6.6280) // Flon McDonald's coordinates
        
        viewModel.updateUserLocation(hardcodedLocation)
        mapManager.updateUserLocationMarker(hardcodedLocation)
        mapManager.updateZoneOverlays(lausanneZones, hardcodedLocation)
    }
    
    fun updateLocationAndZones(location: LatLng) {
        viewModel.updateUserLocation(location)
        mapManager.updateUserLocationMarker(location)
        mapManager.updateZoneOverlays(lausanneZones, location)
    }
    
    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
