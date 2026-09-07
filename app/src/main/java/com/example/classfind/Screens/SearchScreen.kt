package com.example.classfind.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class Classroom(
    val roomNumber: String,
    val building: String,
    val floor: String,
    val capacity: Int,
    val type: String
)

@Composable
fun SearchScreen(
    onClassroomClick: (Classroom) -> Unit,
    onBackClick: () -> Unit
) {
    var searchText by remember {
        mutableStateOf("")
    }

    val classrooms = listOf(
        Classroom("CR 01", "IT Building", "1st Floor", 60, "Lecture Room"),
        Classroom("CR 02", "IT Building", "1st Floor", 60, "Lecture Room"),
        Classroom("CR 03", "IT Building", "1st Floor", 60, "Lecture Room"),
        Classroom("CR 04", "IT Building", "2nd Floor", 50, "Lecture Room"),
        Classroom("IT 06", "IT Building", "2nd Floor", 40, "Computer Lab"),
        Classroom("CR 05", "Academic Block", "1st Floor", 50, "Lecture Room")
    )

    val filteredClassrooms = classrooms.filter { classroom ->
        classroom.roomNumber.contains(searchText, ignoreCase = true) ||
                classroom.building.contains(searchText, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "Search Classrooms",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Search classroom or building")
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${filteredClassrooms.size} results",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredClassrooms) { classroom ->

                ClassroomSearchItem(
                    classroom = classroom,
                    onClick = {
                        onClassroomClick(classroom)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Home")
        }
    }
}

@Composable
fun ClassroomSearchItem(
    classroom: Classroom,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = classroom.roomNumber,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(text = classroom.building)

                Text(text = classroom.floor)

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${classroom.capacity} seats • ${classroom.type}"
                )
            }

            Text(
                text = "›",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}