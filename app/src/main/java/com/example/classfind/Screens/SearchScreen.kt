package com.example.classfind.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchScreen(
    initialQuery: String = "",
    onClassroomClick: (Classroom) -> Unit,
    onBackClick: () -> Unit
) {
    var searchText by remember { mutableStateOf(initialQuery) }
    val focusManager = LocalFocusManager.current

    val allClassrooms = listOf(
        Classroom("IT 01", "IT Building", "Ground Floor", 40, "Lab"),
        Classroom("IT 02", "IT Building", "Ground Floor", 40, "Lab"),
        Classroom("IT 03", "IT Building", "Ground Floor", 40, "Lab"),
        Classroom("IT 04", "IT Building", "1st Floor", 50, "Lecture"),
        Classroom("IT 05", "IT Building", "1st Floor", 50, "Lecture"),
        Classroom("IT 06", "IT Building", "2nd Floor", 60, "Lecture"),
        Classroom("CR 01", "Academic Block", "1st Floor", 30, "Classroom"),
        Classroom("CR 02", "Academic Block", "1st Floor", 30, "Classroom"),
        Classroom("CR 03", "Academic Block", "1st Floor", 30, "Classroom"),
        Classroom("CR 04", "Academic Block", "1st Floor", 30, "Classroom"),
        Classroom("CR 05", "Academic Block", "1st Floor", 30, "Classroom"),
        Classroom("CR 06", "Academic Block", "1st Floor", 30, "Classroom"),
        Classroom("CR 07", "Academic Block", "1st Floor", 30, "Classroom"),
        Classroom("CR 08", "Academic Block", "2nd Floor", 30, "Classroom"),
        Classroom("CR 09", "Academic Block", "2nd Floor", 30, "Classroom"),
        Classroom("CR 10", "Academic Block", "2nd Floor", 30, "Classroom"),
        Classroom("CR 11", "Academic Block", "2nd Floor", 30, "Classroom"),
        Classroom("CR 12", "Academic Block", "2nd Floor", 30, "Classroom"),
        Classroom("CR 13", "Academic Block", "2nd Floor", 30, "Classroom"),
        Classroom("Library", "Architecture Building", "Ground Floor", 200, "Library"),
        Classroom("ARC 01", "Architecture Building", "1st Floor", 40, "Studio"),
        Classroom("ARC 02", "Architecture Building", "1st Floor", 40, "Studio"),
        Classroom("ARC 03", "Architecture Building", "1st Floor", 40, "Studio"),
        Classroom("ARC 04", "Architecture Building", "2nd Floor", 40, "Studio"),
        Classroom("ARC 05", "Architecture Building", "2nd Floor", 40, "Studio"),
        Classroom("ARC 06", "Architecture Building", "2nd Floor", 40, "Studio")
    )

    val filteredClassrooms = allClassrooms.filter {
        val query = searchText.replace(" ", "")
        it.roomNumber.replace(" ", "").contains(query, ignoreCase = true) ||
        it.building.replace(" ", "").contains(query, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Text(
            text = "Search Results",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search classroom...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))
        
        Text(text = "Found ${filteredClassrooms.size} results", color = Color.Gray, fontSize = 14.sp)
        
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredClassrooms) { classroom ->
                ClassroomSearchItem(classroom = classroom, onClick = { onClassroomClick(classroom) })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SearchFilterChip(label = "Filter", icon = Icons.Default.FilterList)
            SearchFilterChip(label = "Nearest", isSelected = true)
            SearchFilterChip(label = "Floor")
        }
    }
}

@Composable
fun SearchFilterChip(label: String, icon: ImageVector? = null, isSelected: Boolean = false) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.White)
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon, 
                    contentDescription = null, 
                    modifier = Modifier.size(16.dp), 
                    tint = if (isSelected) Color.White else Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(text = label, color = if (isSelected) Color.White else Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
fun ClassroomSearchItem(classroom: Classroom, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Business, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(text = classroom.roomNumber, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = classroom.building, color = Color.Gray, fontSize = 12.sp)
        }
        
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
    }
}