package com.example.personalplanner.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.personalplanner.ui.navigation.NavRoutes
import com.example.personalplanner.ui.navigation.bottomNavItems
@Composable
fun BottomBar(navController: NavController) {
    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = false,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(NavRoutes.Home.route)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

