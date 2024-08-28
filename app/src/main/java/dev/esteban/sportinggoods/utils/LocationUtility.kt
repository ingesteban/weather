package dev.esteban.sportinggoods.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

object LocationUtility {

    @SuppressLint("MissingPermission")
    fun getLocation(
        context: Context,
        onLocationGranted: (location: Location) -> Unit,
        onLocationNotGranted: () -> Unit,
    ) {
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    onLocationGranted(location)
                } else {
                    onLocationNotGranted()
                }
            }
            .addOnFailureListener { _ ->
                onLocationNotGranted()
            }

    }
}
