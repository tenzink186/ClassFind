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

@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {

    // Firebase Authentication
    val auth = remember {
        FirebaseAuth.getInstance()
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

        // Display Firebase error
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

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            onClick = {

                // Clear previous error
                errorMessage = ""

                // Validate fields
                if (name.isBlank()) {
                    errorMessage = "Please enter your name."
                    return@Button
                }

                if (studentId.isBlank()) {
                    errorMessage = "Please enter your student ID."
                    return@Button
                }

                if (email.isBlank()) {
                    errorMessage = "Please enter your email."
                    return@Button
                }

                if (password.isBlank()) {
                    errorMessage = "Please enter a password."
                    return@Button
                }

                if (password.length < 6) {
                    errorMessage = "Password must be at least 6 characters."
                    return@Button
                }

                // Start Firebase registration
                isLoading = true

                auth.createUserWithEmailAndPassword(
                    email.trim(),
                    password
                )
                    .addOnSuccessListener {

                        isLoading = false

                        // Firebase account was successfully created
                        onSignUpSuccess()
                    }
                    .addOnFailureListener { exception ->

                        isLoading = false

                        // Display Firebase error
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
            Text("Already have an account? Sign In")
        }
    }
}