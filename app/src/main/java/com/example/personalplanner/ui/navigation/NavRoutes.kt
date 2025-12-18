package com.example.personalplanner.ui.navigation


sealed class NavRoutes(val route: String) {
    object Login : NavRoutes("login_screen")
    object Home : NavRoutes("home_screen")
    object CreateTask : NavRoutes("create_task")
    object Settings : NavRoutes("settings_screen")
}
