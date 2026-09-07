package com.example.classfind

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.Locale

fun getCurrentLocation(
    context: Context,
    onLocationReceived: (Double, Double, String) -> Unit,
    onError: (String) -> Unit
) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        onError("GPS Permission Required")
        return
    }

    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && 
        !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
        onError("Please Enable GPS")
        return
    }

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val cts = CancellationTokenSource()

    // Explicitly request high accuracy fresh location
    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
        .addOnSuccessListener { location ->
            if (location != null) {
                val lat = location.latitude
                val lon = location.longitude
                
                var detailedPlace = ""
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(lat, lon, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val addr = addresses[0]
                        // Combine building name, street, and sub-area for "exact" info
                        val parts = mutableListOf<String>()
                        addr.featureName?.let { if (!it.contains(",") && it.length > 2) parts.add(it) }
                        addr.thoroughfare?.let { parts.add(it) }
                        addr.subLocality?.let { parts.add(it) }
                        
                        detailedPlace = if (parts.isNotEmpty()) {
                            parts.distinct().take(2).joinToString(", ")
                        } else {
                            addr.getAddressLine(0).split(",").take(2).joinToString(",")
                        }
                    }
                } catch (e: Exception) {
                    detailedPlace = "Point: %.4f, %.4f".format(lat, lon)
                }
                
                if (detailedPlace.isEmpty()) detailedPlace = "Campus Area (%.2f)".format(location.accuracy)
                
                onLocationReceived(lat, lon, detailedPlace)
            } else {
                // Fallback to last location if fresh one is null
                fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                    if (lastLoc != null) {
                        onLocationReceived(lastLoc.latitude, lastLoc.longitude, "Approx: Nearby")
                    } else {
                        onError("Locating...")
                    }
                }
            }
        }
        .addOnFailureListener { e ->
            onError("GPS Error: Try again")
        }
}