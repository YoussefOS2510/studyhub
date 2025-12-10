package com.example.studyplannerapp.ui.navigation

import GoogleLoginScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studyplannerapp.data.remote.AuthRepository
import com.example.studyplannerapp.data.prefrences.ThemePreferenceManager
import com.example.studyplannerapp.ui.components.BottomBar
import com.example.studyplannerapp.ui.screens.CreateNewTaskScreen
import com.example.studyplannerapp.ui.screens.SettingsScreen
import com.example.studyplannerapp.ui.screens.StudyHubScreen
import com.example.studyplannerapp.viewmodel.TaskViewModel
import com.example.studyplannerapp.viewmodel.TaskViewModelFactory

@Composable
fun AppNavHost(
    isLoggedIn: Boolean,
    onGoogleLogin: () -> Unit,
    onLogout: () -> Unit,
    themeManager: ThemePreferenceManager,
    isDark: Boolean,
    authRepository:AuthRepository,
    taskViewModelFactory: TaskViewModelFactory

) {
    val navController = rememberNavController()

    /**
     * AUTH FLOW
     */
    if (!isLoggedIn) {
        NavHost(navController, startDestination = NavRoutes.Login.route) {
            composable(NavRoutes.Login.route) {
                GoogleLoginScreen(
                    onGoogleLoginClick = onGoogleLogin,
                    modifier = Modifier
                )
            }
        }
        return
    }

    /**
     * MAIN APP FLOW WITH BOTTOM NAVIGATION
     */
    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->

        // Main NavHost
        NavHost(
            navController = navController,
            startDestination = NavRoutes.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(NavRoutes.Home.route) {
                val viewModel: TaskViewModel = viewModel(factory = taskViewModelFactory)
                StudyHubScreen(
                    onEditTask = { task ->
                        navController.navigate(NavRoutes.CreateTask.route + "/${task.id}")
                    },
                    viewModel = viewModel
                )
            }

            composable(NavRoutes.CreateTask.route + "/{taskId}") { backStack ->
                val taskId = backStack.arguments?.getString("taskId")

                val viewModel: TaskViewModel = viewModel(factory = taskViewModelFactory)

                CreateNewTaskScreen(
                    taskId = taskId ,
                    onCancel = { navController.popBackStack() },
                    onTaskCreated = { navController.popBackStack() },
                    viewModel = viewModel
                )
            }
            composable(NavRoutes.CreateTask.route) {
                val viewModel: TaskViewModel = viewModel(factory = taskViewModelFactory)

                CreateNewTaskScreen(
                    taskId = null,
                    onCancel = { navController.popBackStack() },
                    onTaskCreated = { navController.popBackStack() },
                    viewModel = viewModel
                )
            }
            composable(NavRoutes.Settings.route) {
                val viewModel: TaskViewModel = viewModel(factory = taskViewModelFactory)

                SettingsScreen(
                    onSignOut = onLogout,
                    themeManager = themeManager,
                    isDark = isDark,
                    modifier = Modifier,
                    authRepository = authRepository,
                    viewModel = viewModel
                )
            }
        }
    }
}
