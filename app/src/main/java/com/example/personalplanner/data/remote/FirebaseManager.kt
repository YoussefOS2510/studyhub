package com.example.personalplanner.data.remote


import android.content.Context
import com.example.personalplanner.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


data class UserProfile(
    val displayName: String = "Guest User",
    val email: String = "guest@example.com",
    val photoUrl: String? = null
)

class AuthRepository(private val context: Context) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    // Expose a clean user profile (name + email)
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val googleSignInClient: GoogleSignInClient

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)

        // Listen to auth changes and update both currentUser and userProfile
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            _currentUser.value = user

            _userProfile.value = user?.let {
                UserProfile(
                    displayName = it.displayName ?: "User",
                    email = it.email ?: "no-email@example.com",
                    photoUrl = it.photoUrl?.toString()
                )
            }
        }
    }

    fun getGoogleSignInIntent() = googleSignInClient.signInIntent

    fun signOut() {
        auth.signOut()
        googleSignInClient.signOut()
    }

    fun getAuthInstance(): FirebaseAuth = auth
}