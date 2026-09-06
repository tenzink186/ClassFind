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
        Classroom(
            roomNumber = "CR 01",
            building = "IT Building",
            floor = "1st Floor",
            capacity = 60,
            type = "Lecture Room"
        ),
        Classroom(
            roomNumber = "CR 02",
            building = "IT Building",
            floor = "1st Floor",
            capacity = 60,
            type = "Lecture Room"
        ),
        Classroom(
            roomNumber = "CR 03",
            building = "IT Building",
            floor = "1st Floor",
            capacity = 60,
            type = "Lecture Room"
        ),
        Classroom(
            roomNumber = "CR 04",
            building = "IT Building",
            floor = "2nd Floor",
            capacity = 50,
            type = "Lecture Room"
        ),
        Classroom(
            roomNumber = "IT 06",
            building = "IT Building",
            floor = "2nd Floor",
            capacity = 40,
            type = "Computer Lab"
        ),
        Classroom(
            roomNumber = "CR 05",
            building = "Academic Block",
            floor = "1st Floor",
            capacity = 50,
            type = "Lecture Room"
        )
    )

    val filteredClassrooms = classrooms.filter { classroom ->

        classroom.roomNumber.contains(
            searchText,
            ignoreCase = true
        ) ||
                classroom.building.contains(
                    searchText,
                    ignoreCase = true
                )
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

        Spacer(
            modifier = Modifier.height(16.dp)
        )

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

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = "${filteredClassrooms.size} results",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
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

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = classroom.building
                )

                Text(
                    text = classroom.floor
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

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