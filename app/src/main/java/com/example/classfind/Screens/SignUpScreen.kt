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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onBackToLogin: () -> Unit
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

    var password by remember {
        mutableStateOf("")
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Student Name")
            },
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = studentId,
            onValueChange = {
                studentId = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Student ID")
            },
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Email")
            },
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Password")
            },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        if (errorMessage.isNotEmpty()) {

            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }

        Button(
            onClick = {

                errorMessage = ""

                // Validate name
                if (name.isBlank()) {
                    errorMessage = "Please enter your name."
                    return@Button
                }

                // Validate student ID
                if (studentId.isBlank()) {
                    errorMessage = "Please enter your student ID."
                    return@Button
                }

                // Validate email
                if (email.isBlank()) {
                    errorMessage = "Please enter your email."
                    return@Button
                }

                // Validate password
                if (password.isBlank()) {
                    errorMessage = "Please enter a password."
                    return@Button
                }

                if (password.length < 6) {
                    errorMessage =
                        "Password must be at least 6 characters."
                    return@Button
                }

                isLoading = true

                // Create Firebase Authentication account
                auth.createUserWithEmailAndPassword(
                    email.trim(),
                    password
                )
                    .addOnSuccessListener {

                        val currentUser = auth.currentUser

                        if (currentUser == null) {

                            isLoading = false
                            errorMessage =
                                "Account was created, but user information could not be found."

                            return@addOnSuccessListener
                        }

                        val uid = currentUser.uid

                        // Student information
                        val studentData = hashMapOf(
                            "name" to name.trim(),
                            "studentId" to studentId.trim(),
                            "email" to email.trim(),
                            "uid" to uid
                        )

                        // Save student information in Firestore
                        firestore
                            .collection("students")
                            .document(uid)
                            .set(studentData)
                            .addOnSuccessListener {

                                isLoading = false

                                onSignUpSuccess()
                            }
                            .addOnFailureListener { exception ->

                                isLoading = false

                                errorMessage =
                                    exception.message
                                        ?: "Account created, but student information could not be saved."
                            }
                    }
                    .addOnFailureListener { exception ->

                        isLoading = false

                        errorMessage =
                            exception.message
                                ?: "Account creation failed."
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {

            if (isLoading) {

                CircularProgressIndicator(
                    modifier = Modifier.height(20.dp),
                    strokeWidth = 2.dp
                )

            } else {

                Text("Create Account")
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        TextButton(
            onClick = onBackToLogin,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {

            Text(
                "Already have an account? Sign In"
            )
        }
    }
}