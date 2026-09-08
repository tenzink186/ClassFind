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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit
) {
    val auth = remember { FirebaseAuth.getInstance() }
    val firestore = remember { FirebaseFirestore.getInstance() }
    var emailOrStudentId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var resetMessage by remember { mutableStateOf("") }

    fun performLogin(email: String) {
        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener {
                isLoading = false
                onLoginSuccess()
            }
            .addOnFailureListener {
                isLoading = false
                errorMessage = "Login failed"
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo Placeholder
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Logo",
                tint = Color.White,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "ClassFind",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Welcome Back! 👋",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "Sign in to continue",
            color = Color.Gray,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = emailOrStudentId,
            onValueChange = {
                emailOrStudentId = it
                errorMessage = ""
                resetMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email or Student ID") },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            enabled = !isLoading
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = {
                    if (emailOrStudentId.isBlank()) {
                        errorMessage = "Please enter your email."
                        return@TextButton
                    }
                    isLoading = true
                    auth.sendPasswordResetEmail(emailOrStudentId.trim())
                        .addOnSuccessListener {
                            isLoading = false
                            resetMessage = "Password reset email sent."
                        }
                        .addOnFailureListener { exception ->
                            isLoading = false
                            errorMessage = exception.message ?: "Error sending reset email"
                        }
                },
                enabled = !isLoading
            ) {
                Text("Forgot Password?", color = MaterialTheme.colorScheme.primary)
            }
        }

        if (resetMessage.isNotEmpty()) {
            Text(text = resetMessage, color = MaterialTheme.colorScheme.primary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (emailOrStudentId.isBlank() || password.isBlank()) {
                    errorMessage = "Please fill all fields."
                    return@Button
                }
                isLoading = true

                val input = emailOrStudentId.trim()

                if (input.contains("@")) {
                    // It's an email, login directly
                    performLogin(input)
                } else {
                    // It's a student ID, look up email in Firestore
                    firestore.collection("students")
                        .whereEqualTo("studentId", input)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (querySnapshot.isEmpty) {
                                isLoading = false
                                errorMessage = "Login failed"
                            } else {
                                val doc = querySnapshot.documents[0]
                                val email = doc.getString("email")
                                if (email != null) {
                                    performLogin(email)
                                } else {
                                    isLoading = false
                                    errorMessage = "Login failed"
                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            isLoading = false
                            errorMessage = "Login failed"
                        }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Sign In", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onSignUpClick) {
            Text("Don't have an account? Sign Up", color = Color.Gray)
        }
    }
}