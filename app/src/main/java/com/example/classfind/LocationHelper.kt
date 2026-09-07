package com.example.classfind

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

fun getCurrentLocation(
    context: Context,
    onLocationReceived: (Double, Double) -> Unit,
    onError: (String) -> Unit
) {
    val hasFineLocation = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val hasCoarseLocation = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (!hasFineLocation && !hasCoarseLocation) {
        onError("Location permission has not been granted.")
        return
    }

    val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    fusedLocationClient.getCurrentLocation(
        Priority.PRIORITY_HIGH_ACCURACY,
        null
    )
        .addOnSuccessListener { location ->

            if (location != null) {

                val latitude = location.latitude
                val longitude = location.longitude

                onLocationReceived(
                    latitude,
                    longitude
                )

            } else {
                onError("Unable to get your current location.")
            }
        }
        .addOnFailureListener { exception ->

            onError(
                exception.message
                    ?: "Failed to get current location."
            )
        }
}