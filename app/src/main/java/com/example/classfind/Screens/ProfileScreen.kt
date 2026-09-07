package com.example.classfind.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProfileScreen(
    onLogout: () -> Unit
) {

    val auth = remember {
        FirebaseAuth.getInstance()
    }

    val firestore = remember {
        FirebaseFirestore.getInstance()
    }

    var name by remember {
        mutableStateOf("")
    }

    var studentId by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {

        val currentUser = auth.currentUser

        if (currentUser == null) {
            errorMessage = "No user is currently logged in."
            isLoading = false
            return@LaunchedEffect
        }

        val uid = currentUser.uid

        firestore
            .collection("students")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    name = document.getString("name") ?: ""
                    studentId = document.getString("studentId") ?: ""
                    email = document.getString("email") ?: ""

                } else {

                    errorMessage = "Student information not found."
                }

                isLoading = false
            }
            .addOnFailureListener { exception ->

                errorMessage =
                    exception.message ?: "Failed to load student information."

                isLoading = false
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "My Profile",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        if (isLoading) {

            CircularProgressIndicator()

        } else if (errorMessage.isNotEmpty()) {

            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )

        } else {

            Text(
                text = "Student Name",
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = "Student ID",
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = studentId,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = "Email",
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = email,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(
                modifier = Modifier.height(40.dp)
            )

            Button(
                onClick = {
                    auth.signOut()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }
    }
}