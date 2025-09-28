package yawza.zawya.manager

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

class LocationManager(private val context: Context) {
    
    private val fusedLocationClient: FusedLocationProviderClient = 
        LocationServices.getFusedLocationProviderClient(context)
    
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, 
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    @SuppressLint("MissingPermission")
    fun getCurrentLocation(callback: (LatLng?) -> Unit) {
        if (hasLocationPermission()) {
            try {
                // First try to get last known location (fastest)
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            // Check if location is recent (less than 30 seconds old)
                            val locationAge = System.currentTimeMillis() - location.time
                            if (locationAge < 30000) { // 30 seconds
                                android.util.Log.d("LocationManager", "Using recent cached location: ${location.latitude}, ${location.longitude}")
                                callback(LatLng(location.latitude, location.longitude))
                            } else {
                                android.util.Log.d("LocationManager", "Cached location too old, requesting fresh location")
                                requestFreshLocation(callback)
                            }
                        } else {
                            // No recent location, request fresh location
                            android.util.Log.d("LocationManager", "No cached location, requesting fresh location")
                            requestFreshLocation(callback)
                        }
                    }
                    .addOnFailureListener {
                        // If lastLocation fails, try to get fresh location
                        android.util.Log.d("LocationManager", "Cached location failed, requesting fresh location")
                        requestFreshLocation(callback)
                    }
            } catch (e: SecurityException) {
                // Handle case where permission was revoked at runtime
                callback(null)
            }
        } else {
            callback(null)
        }
    }
    
    @SuppressLint("MissingPermission")
    private fun requestFreshLocation(callback: (LatLng?) -> Unit) {
        try {
            val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                500L // 0.5 seconds for faster response
            ).build()
            
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                object : com.google.android.gms.location.LocationCallback() {
                    override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                        val location = locationResult.lastLocation
                        if (location != null) {
                            fusedLocationClient.removeLocationUpdates(this)
                            android.util.Log.d("LocationManager", "Fresh location received: ${location.latitude}, ${location.longitude}")
                            callback(LatLng(location.latitude, location.longitude))
                        } else {
                            android.util.Log.d("LocationManager", "No fresh location received")
                            callback(null)
                        }
                    }
                },
                null
            )
            
            // Reduce timeout to 3 seconds for faster fallback
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                android.util.Log.d("LocationManager", "Location request timeout, using fallback")
                callback(null)
            }, 3000)
            
        } catch (e: Exception) {
            android.util.Log.e("LocationManager", "Error requesting fresh location", e)
            callback(null)
        }
    }
    
    fun calculateDistance(from: LatLng, to: LatLng): Float {
        val distance = FloatArray(1)
        Location.distanceBetween(
            from.latitude, from.longitude,
            to.latitude, to.longitude,
            distance
        )
        return distance[0]
    }
    
    /**
     * Force a fresh location update by clearing cached location
     */
    @SuppressLint("MissingPermission")
    fun forceLocationUpdate(callback: (LatLng?) -> Unit) {
        android.util.Log.d("LocationManager", "Forcing location update...")
        if (hasLocationPermission()) {
            try {
                // Clear any cached location and request fresh one
                fusedLocationClient.flushLocations()
                requestFreshLocation(callback)
            } catch (e: Exception) {
                android.util.Log.e("LocationManager", "Error forcing location update", e)
                callback(null)
            }
        } else {
            callback(null)
        }
    }
}
