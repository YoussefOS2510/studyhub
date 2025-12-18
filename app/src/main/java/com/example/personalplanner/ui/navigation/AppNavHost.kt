package com.example.personalplanner.ui.navigation

import GoogleLoginScreen
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.personalplanner.data.remote.AuthRepository
import com.example.personalplanner.data.prefrences.ThemePreferenceManager
import com.example.personalplanner.ui.components.BottomBar
import com.example.personalplanner.ui.screens.CreateNewTaskScreen
import com.example.personalplanner.ui.screens.SettingsScreen
import com.example.personalplanner.ui.screens.StudyHubScreen
import com.example.personalplanner.viewmodel.TaskViewModel
import com.example.personalplanner.viewmodel.TaskViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    isLoggedIn: Boolean,
    onGoogleLogin: () -> Unit,
    onLogout: () -> Unit,
    themeManager: ThemePreferenceManager,
    isDark: Boolean,
    authRepository: AuthRepository,
    taskViewModelFactory: TaskViewModelFactory
) {
    val navController = rememberNavController()

    /**
     * AUTH FLOW
     */
    if (!isLoggedIn) {
        val viewModel: TaskViewModel = viewModel(factory = taskViewModelFactory)

        NavHost(navController, startDestination = NavRoutes.Login.route) {
            composable(NavRoutes.Login.route) {
                GoogleLoginScreen(
                    onGoogleLoginClick = onGoogleLogin,
                    modifier = Modifier,
                    viewModel = viewModel
                )
            }
        }
        return
    }

    /**
     * MAIN APP FLOW
     */
    val mainViewModel: TaskViewModel = viewModel(factory = taskViewModelFactory)

    // 1. Observe tasks
    val tasks by mainViewModel.allTasks.collectAsState(initial = emptyList())

    // 2. Calculate both counts
    val totalCount = tasks.size
    val completedCount = tasks.count { it.isFinished }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "<study-app/>",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        // 3. Display as "X/Y Completed"
                        Text(
                            text = "$completedCount/$totalCount Completed",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completed Tasks",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            )
        },
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = NavRoutes.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavRoutes.Home.route) {
                StudyHubScreen(
                    onEditTask = { task ->
                        navController.navigate(NavRoutes.CreateTask.route + "/${task.id}")
                    },
                    viewModel = mainViewModel
                )
            }

            composable(NavRoutes.CreateTask.route + "/{taskId}") { backStack ->
                val taskId = backStack.arguments?.getString("taskId")
                CreateNewTaskScreen(
                    taskId = taskId,
                    onCancel = { navController.popBackStack() },
                    onTaskCreated = { navController.popBackStack() },
                    viewModel = mainViewModel
                )
            }

            composable(NavRoutes.CreateTask.route) {
                CreateNewTaskScreen(
                    taskId = null,
                    onCancel = { navController.popBackStack() },
                    onTaskCreated = { navController.popBackStack() },
                    viewModel = mainViewModel
                )
            }

            composable(NavRoutes.Settings.route) {
                SettingsScreen(
                    onSignOut = onLogout,
                    themeManager = themeManager,
                    isDark = isDark,
                    modifier = Modifier,
                    authRepository = authRepository,
                    viewModel = mainViewModel
                )
            }
        }
    }
}