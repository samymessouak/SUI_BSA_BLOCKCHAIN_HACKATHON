package yawza.zawya.manager

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline
import yawza.zawya.models.StickerZone
import yawza.zawya.utils.ZoneOverlayManager

class MapManager(private val context: Context) {
    
    private var map: GoogleMap? = null
    private lateinit var zoneOverlayManager: ZoneOverlayManager
    private var userMarker: Marker? = null
    private var pathPolyline: Polyline? = null
    
    fun initializeMap(googleMap: GoogleMap) {
        map = googleMap
        zoneOverlayManager = ZoneOverlayManager(context)
        
        // Configure map settings - Pokémon Go style
        googleMap.uiSettings.isZoomControlsEnabled = false
        googleMap.uiSettings.isMyLocationButtonEnabled = false
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isScrollGesturesEnabled = false
        googleMap.uiSettings.isZoomGesturesEnabled = true
        googleMap.uiSettings.isRotateGesturesEnabled = true // Enable rotation
        googleMap.uiSettings.isTiltGesturesEnabled = true // Enable tilt for better 3D view
        
        // Set up camera change listener to enforce zoom restrictions
        @Suppress("DEPRECATION")
        googleMap.setOnCameraChangeListener { cameraPosition ->
            val zoom = cameraPosition.zoom
            if (zoom < 16f || zoom > 20f) {
                // Enforce zoom limits
                val clampedZoom = zoom.coerceIn(16f, 20f)
                googleMap.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.zoomTo(clampedZoom))
            }
        }
    }
    
    fun updateUserLocationMarker(location: LatLng) {
        map?.let { googleMap ->
            userMarker?.remove()
            userMarker = googleMap.addMarker(
                com.google.android.gms.maps.model.MarkerOptions()
                    .position(location)
                    .title("Your Location")
                    .icon(com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(
                        com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE
                    ))
            )
            // Center camera on user location with proper zoom (street level)
            googleMap.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(location, 18f))
        }
    }
    
    fun updateZoneOverlays(zones: List<StickerZone>, userLocation: LatLng?) {
        map?.let { googleMap ->
            // Ensure zoneOverlayManager is initialized
            if (!::zoneOverlayManager.isInitialized) {
                zoneOverlayManager = ZoneOverlayManager(context)
            }
            
            zoneOverlayManager.clearAllZones()
            
            val dynamicZones = zones.map { zone ->
                val distance = userLocation?.let { userLoc ->
                    val distanceArray = FloatArray(1)
                    android.location.Location.distanceBetween(
                        userLoc.latitude, userLoc.longitude,
                        zone.center.latitude, zone.center.longitude,
                        distanceArray
                    )
                    distanceArray[0]
                } ?: Float.MAX_VALUE
                
                // Make zone extremely precise when very close (within 50m)
                val adjustedRadius = when {
                    distance <= 10 -> 5.0 // Very close: 5m radius (almost a point)
                    distance <= 25 -> 10.0 // Close: 10m radius
                    distance <= 50 -> 20.0 // Near: 20m radius
                    distance <= 100 -> zone.radius * 0.5 // Approaching: half radius
                    distance <= 200 -> zone.radius * 0.7 // Getting closer: 70% radius
                    else -> zone.radius // Far away: full radius
                }
                
                zone.copy(radius = adjustedRadius)
            }
            
            // Debug: Log zone information
            android.util.Log.d("MapManager", "Adding ${dynamicZones.size} zones to map")
            dynamicZones.forEach { zone ->
                android.util.Log.d("MapManager", "Zone: ${zone.brandName} at ${zone.center.latitude}, ${zone.center.longitude} with radius ${zone.radius}")
            }
            
            zoneOverlayManager.addZonesToMap(googleMap, dynamicZones)
        }
    }
    
    fun navigateToZone(zone: StickerZone, userLocation: LatLng?) {
        map?.let { googleMap ->
            // Clear existing path
            pathPolyline?.remove()
            
            // Draw path from user to zone
            userLocation?.let { userLoc ->
                val pathPoints = listOf(userLoc, zone.center)
                pathPolyline = googleMap.addPolyline(
                    com.google.android.gms.maps.model.PolylineOptions()
                        .addAll(pathPoints)
                        .color(android.graphics.Color.BLUE)
                        .width(8f)
                        .pattern(listOf(
                            com.google.android.gms.maps.model.Dash(20f),
                            com.google.android.gms.maps.model.Gap(10f)
                        ))
                )
            }
            
            // Show zone briefly
            googleMap.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(zone.center, 16f))
        }
    }
    
    fun returnToUserLocation(userLocation: LatLng) {
        map?.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(userLocation, 18f))
        pathPolyline?.remove()
        pathPolyline = null
    }
    
    fun zoomIn() {
        map?.let { googleMap ->
            val currentZoom = googleMap.cameraPosition.zoom
            if (currentZoom < 20f) { // Max zoom: very close street level
                googleMap.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.zoomIn())
            }
        }
    }
    
    fun zoomOut() {
        map?.let { googleMap ->
            val currentZoom = googleMap.cameraPosition.zoom
            if (currentZoom > 16f) { // Min zoom: ~300m radius around user
                googleMap.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.zoomOut())
            }
        }
    }
    
    fun centerOnUser(userLocation: LatLng) {
        map?.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(userLocation, 18f))
    }
    
    fun centerOnUserWithBearing(userLocation: LatLng, bearing: Float) {
        map?.let { googleMap ->
            val cameraPosition = com.google.android.gms.maps.model.CameraPosition.Builder()
                .target(userLocation)
                .zoom(18f) // Street level zoom
                .bearing(bearing)
                .tilt(45f) // Slight tilt for better 3D view
                .build()
            googleMap.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }
    
    fun rotateAroundUser(userLocation: LatLng, bearing: Float) {
        map?.let { googleMap ->
            val cameraPosition = com.google.android.gms.maps.model.CameraPosition.Builder()
                .target(userLocation)
                .zoom(googleMap.cameraPosition.zoom) // Keep current zoom level
                .bearing(bearing)
                .tilt(googleMap.cameraPosition.tilt) // Keep current tilt
                .build()
            googleMap.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }
    
    fun setMapClickListener(listener: GoogleMap.OnMapClickListener) {
        map?.setOnMapClickListener(listener)
    }
    
    fun getCurrentBearing(): Float {
        return map?.cameraPosition?.bearing ?: 0f
    }
    
    fun updateZonePrecision(userLocation: LatLng, zones: List<StickerZone>) {
        // This method can be called to update zone precision in real-time
        updateZoneOverlays(zones, userLocation)
    }
}
