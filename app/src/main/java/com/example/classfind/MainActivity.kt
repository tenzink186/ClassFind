package com.example.classfind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.classfind.screens.HomeScreen
import com.example.classfind.screens.LoginScreen
import com.example.classfind.screens.SignUpScreen
import com.example.classfind.ui.theme.ClassFindTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            ClassFindTheme {

                ClassFindApp()

            }
        }
    }
}

@Composable
fun ClassFindApp() {

    var currentScreen by remember {
        mutableStateOf("login")
    }

    when (currentScreen) {

        "login" -> {

            LoginScreen(

                onLoginSuccess = {
                    currentScreen = "home"
                },

                onSignUpClick = {
                    currentScreen = "signup"
                }

            )
        }

        "signup" -> {

            SignUpScreen(

                onSignUpSuccess = {
                    currentScreen = "home"
                },

                onBackToLogin = {
                    currentScreen = "login"
                }

            )
        }

        "home" -> {

            HomeScreen()

        }
    }
}