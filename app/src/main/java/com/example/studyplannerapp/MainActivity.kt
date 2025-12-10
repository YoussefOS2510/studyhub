package com.example.studyplannerapp

import StudyplannerappTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.room.Room
import com.example.studyplannerapp.data.local.database.AppDatabase
import com.example.studyplannerapp.data.remote.AuthRepository
import com.example.studyplannerapp.data.prefrences.ThemePreferenceManager
import com.example.studyplannerapp.data.remote.FirestoreService
import com.example.studyplannerapp.data.repository.TaskRepository
import com.example.studyplannerapp.ui.navigation.AppNavHost
import com.example.studyplannerapp.viewmodel.TaskViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : ComponentActivity() {

    // Make database a companion object so it's accessible globally (safe for singletons)
    companion object {
        lateinit var taskDatabase: AppDatabase
            private set
    }

    private lateinit var themeManager: ThemePreferenceManager
    private lateinit var authRepository: AuthRepository

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
                    if (!signInTask.isSuccessful) {
                        signInTask.exception?.printStackTrace()
                    }
                }
        } catch (e: ApiException) {
            e.printStackTrace()
        }
    }
    val firestore = FirestoreService()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Room Database (singleton)
        taskDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "study_planner_db"
        )
            .fallbackToDestructiveMigration() // Only for development! Remove in production
            .build()

        themeManager = ThemePreferenceManager(applicationContext)
        authRepository = AuthRepository(applicationContext)
        val taskRepository = TaskRepository(
            dao = taskDatabase.taskDao(),
            firestore = firestore
        )

        val taskViewModelFactory = TaskViewModelFactory(
            repository = taskRepository,
            authRepository = authRepository
        )
        setContent {
            val isDark by themeManager.isDarkThemeEnabled.collectAsState(initial = false)
            val currentUser by authRepository.currentUser.collectAsState(initial = null)

            StudyplannerappTheme(darkTheme = isDark) {
                AppNavHost(
                    isLoggedIn = currentUser != null,
                    onGoogleLogin = {
                        launcher.launch(authRepository.getGoogleSignInIntent())
                    },
                    onLogout = {
                        authRepository.signOut()
                    },
                    themeManager = themeManager,
                    isDark = isDark,
                    authRepository = authRepository,
                    taskViewModelFactory = taskViewModelFactory     // ← NEW
                )

            }
        }
    }
}