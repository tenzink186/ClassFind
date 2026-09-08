package com.example.classfind.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.classfind.getCurrentLocation

enum class MapState {
    CAMPUS_VIEW,
    BUILDING_VIEW,
    DIRECTIONS_VIEW
}

@Composable
fun CampusMapScreen(
    @Suppress("UNUSED_PARAMETER") selectedClassroom: Classroom?,
    initialBuilding: String? = null,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var mapState by remember { 
        mutableStateOf(if (initialBuilding != null) MapState.BUILDING_VIEW else MapState.CAMPUS_VIEW) 
    }
    var selectedBuilding by remember { mutableStateOf(initialBuilding) }
    var currentPlaceName by remember { mutableStateOf("Tap to get location") }
    var currentCoords by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var isLoadingLocation by remember { mutableStateOf(false) }

    fun performLocationRequest() {
        val fineLocationGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (fineLocationGranted) {
            isLoadingLocation = true
            getCurrentLocation(context, { lat, lon, placeName ->
                currentPlaceName = placeName
                currentCoords = Pair(lat, lon)
                isLoadingLocation = false
            }, { error ->
                currentPlaceName = error
                isLoadingLocation = false
            })
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            performLocationRequest()
        } else {
            currentPlaceName = "Precise Location Denied"
        }
    }

    LaunchedEffect(Unit) {
        val fineLocationGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (fineLocationGranted) {
            performLocationRequest()
        } else {
            locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    LaunchedEffect(initialBuilding) {
        if (initialBuilding != null) {
            selectedBuilding = initialBuilding
            mapState = MapState.BUILDING_VIEW
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (mapState) {
            MapState.CAMPUS_VIEW -> CampusView(
                currentPlace = currentPlaceName,
                isLoading = isLoadingLocation,
                coords = currentCoords,
                onBuildingClick = { building ->
                    selectedBuilding = building
                    mapState = MapState.BUILDING_VIEW
                },
                onSearchAction = { query ->
                    if (query.contains("IT", ignoreCase = true)) {
                        selectedBuilding = "IT Building"
                        mapState = MapState.BUILDING_VIEW
                    } else if (query.contains("CR", ignoreCase = true) || query.contains("Academic", ignoreCase = true)) {
                        selectedBuilding = "Academic Block"
                        mapState = MapState.BUILDING_VIEW
                    } else if (query.contains("ARC", ignoreCase = true) || query.contains("Arch", ignoreCase = true)) {
                        selectedBuilding = "Architecture Building"
                        mapState = MapState.BUILDING_VIEW
                    }
                },
                onStartNavClick = { mapState = MapState.DIRECTIONS_VIEW },
                onBackClick = onBackClick,
                onRefreshLocation = { performLocationRequest() }
            )
            MapState.BUILDING_VIEW -> BuildingView(
                buildingName = selectedBuilding ?: "IT Building",
                onBackClick = { mapState = MapState.CAMPUS_VIEW }
            )
            MapState.DIRECTIONS_VIEW -> DirectionsView(
                startLocation = currentPlaceName,
                onBackClick = { mapState = MapState.CAMPUS_VIEW }
            )
        }
    }
}

@Composable
fun CampusView(
    currentPlace: String,
    isLoading: Boolean,
    coords: Pair<Double, Double>?,
    onBuildingClick: (String) -> Unit,
    onSearchAction: (String) -> Unit,
    onStartNavClick: () -> Unit,
    onBackClick: () -> Unit,
    onRefreshLocation: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "Campus Map", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.AutoMirrored.Filled.FormatListBulleted, contentDescription = null)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search bar
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search buildings or rooms...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearchAction(searchText) }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Map Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF1F8E9))
            ) {
                Box(modifier = Modifier.fillMaxHeight().width(24.dp).background(Color(0xFFE0E0E0)).align(Alignment.Center))
                Box(modifier = Modifier.fillMaxWidth().height(24.dp).background(Color(0xFFE0E0E0)).align(Alignment.Center))

                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        BuildingBlock(
                            name = "Academic Block",
                            subText = "13 Classrooms\n2 Storeys",
                            color = Color(0xFFBBDEFB),
                            modifier = Modifier.size(140.dp, 90.dp),
                            onClick = { onBuildingClick("Academic Block") }
                        )
                        BuildingBlock(
                            name = "IT Building",
                            subText = "6 Classrooms\n3 Storeys",
                            color = Color(0xFFC8E6C9),
                            modifier = Modifier.size(140.dp, 90.dp),
                            onClick = { onBuildingClick("IT Building") }
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            BuildingBlock(name = "FabLab", subText = "", color = Color(0xFFE0E0E0), modifier = Modifier.size(70.dp, 40.dp), onClick = { onBuildingClick("FabLab") })
                            Spacer(modifier = Modifier.width(8.dp))
                            BuildingBlock(name = "LH1", subText = "1 Classroom", color = Color(0xFFFFECB3), modifier = Modifier.size(60.dp, 40.dp), onClick = { onBuildingClick("LH1") })
                        }
                        
                        Spacer(modifier = Modifier.weight(1f))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            BuildingBlock(name = "LH2", subText = "1 Classroom", color = Color(0xFFFFECB3), modifier = Modifier.size(60.dp, 40.dp), onClick = { onBuildingClick("LH2") })
                            Spacer(modifier = Modifier.width(8.dp))
                            BuildingBlock(name = "LH3", subText = "1 Classroom", color = Color(0xFFFFECB3), modifier = Modifier.size(60.dp, 40.dp), onClick = { onBuildingClick("LH3") })
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    BuildingBlock(
                        name = "Architecture Building",
                        subText = "Library & 6 Classrooms\n3 Storeys",
                        color = Color(0xFFF8BBD0),
                        modifier = Modifier.fillMaxWidth(0.7f).height(90.dp).align(Alignment.CenterHorizontally),
                        onClick = { onBuildingClick("Architecture Building") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom controls
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    LocationPill(
                        text = if (isLoading) "Locating..." else currentPlace,
                        isSelected = true,
                        onClick = onRefreshLocation
                    )
                    if (coords != null && !isLoading) {
                        Text(
                            text = "${"%.5f".format(coords.first)}, ${"%.5f".format(coords.second)}",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 12.dp, top = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                LocationPill(text = "My Location")
            }
        }

        FloatingActionButton(
            onClick = onStartNavClick,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            containerColor = Color(0xFF6200EE),
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.AutoMirrored.Filled.CallMade, contentDescription = "Navigate")
        }
    }
}

@Composable
fun BuildingBlock(name: String, subText: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.Center) {
            Text(text = name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(text = subText, fontSize = 10.sp, color = Color.Gray, lineHeight = 12.sp)
        }
    }
}

@Composable
fun LocationPill(text: String, isSelected: Boolean = false, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.White)
            .border(1.dp, if (isSelected) Color.Transparent else Color.LightGray, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = text, color = if (isSelected) Color.White else Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun BuildingView(buildingName: String, onBackClick: () -> Unit) {
    var selectedFloor by remember { mutableStateOf("1F") }
    var selectedRoomName by remember { mutableStateOf<String?>(null) }

    val floorOptions = when (buildingName) {
        "IT Building" -> listOf("1F", "2F", "3F")
        "Academic Block" -> listOf("1F", "2F")
        "Architecture Building" -> listOf("1F", "2F", "3F")
        else -> listOf("1F")
    }

    if (selectedFloor !in floorOptions) selectedFloor = "1F"

    val rooms = when (buildingName) {
        "IT Building" -> when (selectedFloor) {
            "1F" -> listOf("IT 01", "IT 02", "IT 03")
            "2F" -> listOf("IT 04", "IT 05")
            "3F" -> listOf("IT 06")
            else -> emptyList()
        }
        "Academic Block" -> when (selectedFloor) {
            "1F" -> listOf("CR 01", "CR 02", "CR 03", "CR 04", "CR 05", "CR 06", "CR 07")
            "2F" -> listOf("CR 08", "CR 09", "CR 10", "CR 11", "CR 12", "CR 13")
            else -> emptyList()
        }
        "Architecture Building" -> when (selectedFloor) {
            "1F" -> listOf("Main Library")
            "2F" -> listOf("ARC 01", "ARC 02", "ARC 03")
            "3F" -> listOf("ARC 04", "ARC 05", "ARC 06")
            else -> emptyList()
        }
        "LH1", "LH2", "LH3" -> listOf("${buildingName} Room")
        else -> listOf("Classroom")
    }

    Column(modifier = Modifier.padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = buildingName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            floorOptions.forEach { floor ->
                FloorChip(floor = floor, isSelected = floor == selectedFloor, onClick = { selectedFloor = floor })
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .border(1.dp, Color.Black, RoundedCornerShape(24.dp))
                .background(Color.White, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.Top, modifier = Modifier.fillMaxSize()) {
                rooms.chunked(2).forEach { rowRooms ->
                    Row(modifier = Modifier.fillMaxWidth().height(100.dp).padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        rowRooms.forEach { roomName ->
                            RoomBox(
                                name = roomName, 
                                isSelected = selectedRoomName == roomName,
                                onClick = { selectedRoomName = if (selectedRoomName == roomName) null else roomName },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowRooms.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
                
                if (rooms.isEmpty()) {
                    Text(text = "No rooms on this floor", modifier = Modifier.align(Alignment.CenterHorizontally), color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            LocationPill(if (selectedRoomName != null) "You chose here" else "You are here", isSelected = true)
            if (selectedRoomName != null) {
                LocationPill(selectedRoomName!!)
            } else {
                LocationPill("Path")
            }
        }
    }
}

@Composable
fun RoomBox(name: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFF6200EE) else Color.Transparent, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name, 
            fontSize = 14.sp, 
            fontWeight = FontWeight.Bold, 
            color = if (isSelected) Color.White else Color.Black
        )
    }
}

@Composable
fun FloorChip(floor: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFF0F0F0))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = floor, color = if (isSelected) Color.White else Color.Black)
    }
}

@Composable
fun DirectionsView(startLocation: String, onBackClick: () -> Unit) {
    Column(modifier = Modifier.padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) }
            Text(text = "Directions", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.CallMade, contentDescription = null)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "From", color = Color.Gray, modifier = Modifier.width(40.dp))
                    Text(text = startLocation, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "To", color = Color.Gray, modifier = Modifier.width(40.dp))
                    Text(text = "IT 06, 3rd Floor", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.MyLocation, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TransportChip("Recommended", isSelected = true)
            TransportChip("Accessible")
            TransportChip("Stairs")
        }
        Spacer(modifier = Modifier.height(24.dp))
        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            val steps = listOf("Head straight from the main entrance" to "25 m", "Turn right after the Admin Block" to "35 m", "Enter the IT Building" to "10 m", "Take the stairs/elevator to 3rd Floor" to "25 m", "Turn left and the room is on your right" to "5 m")
            itemsIndexed(steps) { index, step -> DirectionStep(index + 1, step.first, step.second) }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(text = "Total Distance", color = Color.Gray, fontSize = 12.sp)
                Text(text = "100 m Approx. 3 mins", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = { /* TODO */ }, shape = RoundedCornerShape(12.dp), modifier = Modifier.height(50.dp).width(100.dp)) { Text(text = "Start") }
        }
    }
}

@Composable
fun TransportChip(label: String, isSelected: Boolean = false) {
    Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFF0F0F0)).padding(horizontal = 12.dp, vertical = 6.dp)) {
        Text(text = label, color = if (isSelected) Color.White else Color.Gray, fontSize = 12.sp)
    }
}

@Composable
fun DirectionStep(number: Int, instruction: String, distance: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary), contentAlignment = Alignment.Center) { Text(text = number.toString(), color = Color.White, fontSize = 12.sp) }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = instruction, modifier = Modifier.weight(1f), fontSize = 14.sp)
        Text(text = distance, color = Color.Gray, fontSize = 12.sp)
    }
}