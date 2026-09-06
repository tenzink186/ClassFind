package com.example.classfind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.classfind.screens.CampusMapScreen
import com.example.classfind.screens.Classroom
import com.example.classfind.screens.ClassroomDetailsScreen
import com.example.classfind.screens.HomeScreen
import com.example.classfind.screens.LoginScreen
import com.example.classfind.screens.SearchScreen
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

    var selectedClassroom by remember {
        mutableStateOf<Classroom?>(null)
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

            HomeScreen(
                onFindClassroomClick = { searchText ->

                    currentScreen = "search"
                },
                onCampusMapClick = {

                    currentScreen = "map"
                }
            )
        }

        "search" -> {

            SearchScreen(
                onClassroomClick = { classroom ->

                    selectedClassroom = classroom
                    currentScreen = "details"
                },
                onBackClick = {

                    currentScreen = "home"
                }
            )
        }

        "details" -> {

            selectedClassroom?.let { classroom ->

                ClassroomDetailsScreen(
                    classroom = classroom,

                    onGetDirectionsClick = {

                        currentScreen = "map"
                    },

                    onBackClick = {

                        currentScreen = "search"
                    }
                )
            }
        }

        "map" -> {

            CampusMapScreen(
                selectedClassroom = selectedClassroom,

                onBackClick = {

                    currentScreen = "home"
                }
            )
        }
    }
}