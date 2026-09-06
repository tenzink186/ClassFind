package com.example.classfind.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun CampusMapScreen(
    selectedClassroom: Classroom?,
    onBackClick: () -> Unit
) {

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

            // Academic Block
            MapBuilding(
                name = "Academic Block",
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(20.dp)
            )

            // IT Building
            MapBuilding(
                name = "IT Building",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(20.dp)
            )

            // FabLab
            MapBuilding(
                name = "FabLab",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            )

            // Architecture Building
            MapBuilding(
                name = "Architecture Building",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
            )

            // Current location
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
            modifier = Modifier.height(16.dp)
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