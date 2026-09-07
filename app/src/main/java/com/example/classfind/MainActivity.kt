package com.example.classfind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.classfind.screens.CampusMapScreen
import com.example.classfind.screens.Classroom
import com.example.classfind.screens.ClassroomDetailsScreen
import com.example.classfind.screens.EditProfileScreen
import com.example.classfind.screens.HomeScreen
import com.example.classfind.screens.LoginScreen
import com.example.classfind.screens.ProfileScreen
import com.example.classfind.screens.SearchScreen
import com.example.classfind.screens.SignUpScreen
import com.example.classfind.ui.theme.ClassFindTheme
import com.google.firebase.auth.FirebaseAuth

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

    var targetBuilding by remember {
        mutableStateOf<String?>(null)
    }

    var initialSearchQuery by remember {
        mutableStateOf("")
    }

    val showBottomBar = currentScreen in listOf("home", "map", "profile", "search", "details", "edit_profile")

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentScreen == "home",
                        onClick = { currentScreen = "home" },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == "map",
                        onClick = { currentScreen = "map" },
                        icon = { Icon(Icons.Default.LocationOn, contentDescription = "Map") },
                        label = { Text("Map") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == "profile" || currentScreen == "edit_profile",
                        onClick = { currentScreen = "profile" },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile") }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {

                // ---------------- LOGIN ----------------

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

                // ---------------- SIGN UP ----------------

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

                // ---------------- HOME ----------------

                "home" -> {

                    HomeScreen(
                        onFindClassroomClick = { query ->
                            initialSearchQuery = query
                            currentScreen = "search"
                        },
                        onCampusMapClick = {
                            targetBuilding = null
                            currentScreen = "map"
                        },
                        onProfileClick = {
                            currentScreen = "profile"
                        },
                        onBuildingsClick = {
                            targetBuilding = "IT Building" // Default to IT Building as per Figma
                            currentScreen = "map"
                        }
                    )
                }

                // ---------------- SEARCH ----------------

                "search" -> {

                    BackHandler {
                        currentScreen = "home"
                    }

                    SearchScreen(
                        initialQuery = initialSearchQuery,
                        onClassroomClick = { classroom ->

                            selectedClassroom = classroom

                            currentScreen = "details"
                        },
                        onBackClick = {
                            currentScreen = "home"
                        }
                    )
                }

                // ---------------- CLASSROOM DETAILS ----------------

                "details" -> {

                    BackHandler {
                        currentScreen = "search"
                    }

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

                // ---------------- CAMPUS MAP ----------------

                "map" -> {

                    BackHandler {
                        currentScreen = "home"
                    }

                    CampusMapScreen(
                        selectedClassroom = selectedClassroom,
                        initialBuilding = targetBuilding,
                        onBackClick = {
                            currentScreen = "home"
                        }
                    )
                }

                // ---------------- PROFILE ----------------

                "profile" -> {

                    BackHandler {
                        currentScreen = "home"
                    }

                    ProfileScreen(
                        onLogoutClick = {

                            FirebaseAuth
                                .getInstance()
                                .signOut()

                            currentScreen = "login"
                        },
                        onEditProfileClick = {
                            currentScreen = "edit_profile"
                        },
                        onBackClick = {
                            currentScreen = "home"
                        }
                    )
                }

                "edit_profile" -> {
                    BackHandler {
                        currentScreen = "profile"
                    }

                    EditProfileScreen(
                        onSaveSuccess = {
                            currentScreen = "profile"
                        },
                        onBackClick = {
                            currentScreen = "profile"
                        }
                    )
                }
            }
        }
    }
}
