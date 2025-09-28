package yawza.zawya.controller

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
        // Try to get location with multiple fallback methods
        android.util.Log.d("LocationController", "Requesting location with fallbacks...")
        
        // Method 1: Try fresh location update
        locationManager.forceLocationUpdate { location ->
            if (location != null) {
                android.util.Log.d("LocationController", "Fresh GPS location found: ${location.latitude}, ${location.longitude}")
                viewModel.updateUserLocation(location)
                mapManager.updateUserLocationMarker(location)
                mapManager.updateZoneOverlays(lausanneZones, location)
            } else {
                android.util.Log.d("LocationController", "Fresh GPS failed, trying regular location...")
                // Method 2: Try regular location
                locationManager.getCurrentLocation { fallbackLocation ->
                    if (fallbackLocation != null) {
                        android.util.Log.d("LocationController", "Regular GPS location found: ${fallbackLocation.latitude}, ${fallbackLocation.longitude}")
                        viewModel.updateUserLocation(fallbackLocation)
                        mapManager.updateUserLocationMarker(fallbackLocation)
                        mapManager.updateZoneOverlays(lausanneZones, fallbackLocation)
                    } else {
                        android.util.Log.d("LocationController", "All GPS methods failed, using emulator location fallback")
                        // Method 3: Use emulator location directly (this should work)
                        useEmulatorLocationFallback()
                    }
                }
            }
        }
    }
    
    private fun useEmulatorLocationFallback() {
        // Try to get emulator location from system
        try {
            val locationManager = activity.getSystemService(android.content.Context.LOCATION_SERVICE) as android.location.LocationManager
            val providers = locationManager.getProviders(true)
            
            for (provider in providers) {
                // Check if we have location permission before calling getLastKnownLocation
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    val location = locationManager.getLastKnownLocation(provider)
                    if (location != null) {
                        val latLng = LatLng(location.latitude, location.longitude)
                        android.util.Log.d("LocationController", "Emulator location found: ${latLng.latitude}, ${latLng.longitude}")
                        viewModel.updateUserLocation(latLng)
                        mapManager.updateUserLocationMarker(latLng)
                        mapManager.updateZoneOverlays(lausanneZones, latLng)
                        return
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("LocationController", "Emulator location failed", e)
        }
        
        // Final fallback to Lausanne
        android.util.Log.d("LocationController", "Using hardcoded Lausanne fallback")
        val hardcodedLocation = LatLng(46.5250, 6.6280) // Lausanne center
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
