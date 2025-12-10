package com.example.studyplannerapp.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)



val bottomNavItems = listOf(
    BottomNavItem("Home", NavRoutes.Home.route, Icons.Default.Home),
    BottomNavItem("Create Task", NavRoutes.CreateTask.route, Icons.Default.Add),
    BottomNavItem("Settings", NavRoutes.Settings.route, Icons.Default.Settings)
)
