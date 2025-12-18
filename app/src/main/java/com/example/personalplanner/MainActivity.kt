package com.example.personalplanner

import StudyplannerappTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.room.Room
import com.example.personalplanner.data.local.database.AppDatabase
import com.example.personalplanner.data.remote.AuthRepository
import com.example.personalplanner.data.remote.FirestoreService
import com.example.personalplanner.data.prefrences.ThemePreferenceManager
import com.example.personalplanner.data.repository.TaskRepository
import com.example.personalplanner.ui.navigation.AppNavHost
import com.example.personalplanner.viewmodel.TaskViewModelFactory
import com.example.personalplanner.viewmodel.TaskViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {

    companion object {
        lateinit var taskDatabase: AppDatabase
            private set
    }

    private lateinit var themeManager: ThemePreferenceManager
    private lateinit var authRepository: AuthRepository
    private lateinit var firestore: FirestoreService
    private lateinit var taskRepository: TaskRepository
    private lateinit var taskViewModelFactory: TaskViewModelFactory

    // Google Sign-In launcher
    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            authRepository.getAuthInstance()
                .signInWithCredential(credential)
                .addOnCompleteListener(this) { signInTask ->
                    if (signInTask.isSuccessful) {
                        // Sync Firestore → Room after login
                        val viewModel = ViewModelProvider(this, taskViewModelFactory)
                            .get(TaskViewModel::class.java)
                        viewModel.syncFromCloud()
                    } else {
                        signInTask.exception?.printStackTrace()
                    }
                }
        } catch (e: ApiException) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Room Database
        taskDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "study_planner_db"
        )
            .fallbackToDestructiveMigration()
            .build()

        // Initialize other dependencies
        themeManager = ThemePreferenceManager(applicationContext)
        authRepository = AuthRepository(applicationContext)
        firestore = FirestoreService()
        taskRepository = TaskRepository(
            dao = taskDatabase.taskDao(),
            firestore = firestore
        )
        taskViewModelFactory = TaskViewModelFactory(
            repository = taskRepository,
            authRepository = authRepository
        )

        // Auto-sync if user is already logged in
        authRepository.currentUser.value?.let {
            val viewModel = ViewModelProvider(this, taskViewModelFactory)
                .get(TaskViewModel::class.java)
            viewModel.syncFromCloud()
        }

        // Compose content
        setContent {
            val isDark by themeManager.isDarkThemeEnabled.collectAsState(initial = false)
            val currentUser by authRepository.currentUser.collectAsState(initial = null)

            StudyplannerappTheme(darkTheme = isDark) {
                AppNavHost(
                    isLoggedIn = currentUser != null,
                    onGoogleLogin = { launcher.launch(authRepository.getGoogleSignInIntent()) },
                    onLogout = { authRepository.signOut() },
                    themeManager = themeManager,
                    isDark = isDark,
                    authRepository = authRepository,
                    taskViewModelFactory = taskViewModelFactory
                )
            }
        }
    }
}
