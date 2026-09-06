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
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit
) {

    // Firebase Authentication
    val auth = remember {
        FirebaseAuth.getInstance()
    }

    var emailOrStudentId by remember {
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

    var resetMessage by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "ClassFind",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Welcome Back! 👋",
            fontSize = 28.sp
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Sign in to continue"
        )

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        OutlinedTextField(
            value = emailOrStudentId,
            onValueChange = {
                emailOrStudentId = it
                errorMessage = ""
                resetMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Email or Student ID")
            },
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = ""
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
            modifier = Modifier.height(8.dp)
        )

        TextButton(
            onClick = {

                errorMessage = ""
                resetMessage = ""

                if (emailOrStudentId.isBlank()) {
                    errorMessage = "Please enter your email."
                    return@TextButton
                }

                if (!emailOrStudentId.contains("@")) {
                    errorMessage =
                        "Student ID login will be added after Firestore setup."
                    return@TextButton
                }

                if (password.isBlank()) {
                    errorMessage = "Please enter your password."
                    return@TextButton
                }

                isLoading = true

                auth.sendPasswordResetEmail(
                    emailOrStudentId.trim()
                )
                    .addOnSuccessListener {

                        isLoading = false

                        resetMessage =
                            "Password reset email sent. Check your email."
                    }
                    .addOnFailureListener { exception ->

                        isLoading = false

                        errorMessage =
                            exception.message
                                ?: "Could not send password reset email."
                    }
            },
            enabled = !isLoading
        ) {
            Text("Forgot Password?")
        }

        if (resetMessage.isNotEmpty()) {

            Text(
                text = resetMessage,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }

        if (errorMessage.isNotEmpty()) {

            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            onClick = {

                errorMessage = ""
                resetMessage = ""

                if (emailOrStudentId.isBlank()) {
                    errorMessage = "Please enter your email."
                    return@Button
                }

                if (password.isBlank()) {
                    errorMessage = "Please enter your password."
                    return@Button
                }

                // Student ID login will be connected later
                if (!emailOrStudentId.contains("@")) {
                    errorMessage =
                        "Please use your email for now. Student ID login will be added with Firestore."
                    return@Button
                }

                isLoading = true

                // Firebase Email/Password Login
                auth.signInWithEmailAndPassword(
                    emailOrStudentId.trim(),
                    password
                )
                    .addOnSuccessListener {

                        isLoading = false

                        // Login successful
                        onLoginSuccess()
                    }
                    .addOnFailureListener { exception ->

                        isLoading = false

                        errorMessage =
                            exception.message
                                ?: "Login failed."
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

                Text("Sign In")
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        TextButton(
            onClick = onSignUpClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Don't have an account? Sign Up")
        }
    }
}