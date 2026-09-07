package com.example.classfind.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import com.example.classfind.getCurrentLocation

@Composable
fun CampusMapScreen(
    selectedClassroom: Classroom?,
    onBackClick: () -> Unit
) {

    val context = LocalContext.current

    var latitude by remember {
        mutableStateOf<Double?>(null)
    }

    var longitude by remember {
        mutableStateOf<Double?>(null)
    }

    var locationMessage by remember {
        mutableStateOf("Location not detected yet.")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val fineLocation =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

            val coarseLocation =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (fineLocation || coarseLocation) {

                isLoading = true
                locationMessage = "Getting your current location..."

                getCurrentLocation(
                    context = context,
                    onLocationReceived = { lat, lon ->

                        latitude = lat
                        longitude = lon

                        locationMessage = "Location found!"
                        isLoading = false
                    },
                    onError = { error ->

                        locationMessage = error
                        isLoading = false
                    }
                )

            } else {

                locationMessage =
                    "Location permission was denied."
            }
        }

    fun getLocation() {

        val fineLocationGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (fineLocationGranted || coarseLocationGranted) {

            isLoading = true
            locationMessage = "Getting your current location..."

            getCurrentLocation(
                context = context,
                onLocationReceived = { lat, lon ->

                    latitude = lat
                    longitude = lon

                    locationMessage = "Location found!"
                    isLoading = false
                },
                onError = { error ->

                    locationMessage = error
                    isLoading = false
                }
            )

        } else {

            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Campus Map",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = "Search campus",
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant
                )
        ) {

            Text(
                text = "CAMPUS MAP",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.align(Alignment.Center)
            )

            MapBuilding(
                name = "Academic Block",
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(20.dp)
            )

            MapBuilding(
                name = "IT Building",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(20.dp)
            )

            MapBuilding(
                name = "FabLab",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            )

            MapBuilding(
                name = "Architecture Building",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
            )

            // Current location marker
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.primary
                        )
                )

                Text(
                    text = "You are here",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // GPS Location Button
        Button(
            onClick = {
                getLocation()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {

            Text(
                text = if (isLoading) {
                    "Getting Location..."
                } else {
                    "📍 Get My Current Location"
                }
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        // Location information
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Current Location",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                if (latitude != null && longitude != null) {

                    Text(
                        text = "Latitude: %.6f".format(latitude),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "Longitude: %.6f".format(longitude),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = locationMessage,
                        color = MaterialTheme.colorScheme.primary
                    )

                } else {

                    Text(
                        text = locationMessage,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        if (selectedClassroom != null) {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Column {

                        Text(
                            text = selectedClassroom.roomNumber,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            text = selectedClassroom.building
                        )

                        Text(
                            text = selectedClassroom.floor
                        )
                    }

                    Text(
                        text = "📍",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }

        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Back")
        }
    }
}

@Composable
fun MapBuilding(
    name: String,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier
    ) {

        Text(
            text = name,
            modifier = Modifier.padding(12.dp)
        )
    }
}