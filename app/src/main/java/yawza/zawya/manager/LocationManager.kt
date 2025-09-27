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
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        location?.let {
                            callback(LatLng(it.latitude, it.longitude))
                        } ?: callback(null)
                    }
                    .addOnFailureListener {
                        callback(null)
                    }
            } catch (e: SecurityException) {
                // Handle case where permission was revoked at runtime
                callback(null)
            }
        } else {
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
}
