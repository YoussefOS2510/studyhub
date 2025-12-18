package com.example.personalplanner.data.remote

import com.google.firebase.firestore.FirebaseFirestore

class FirestoreService {
    private val db = FirebaseFirestore.getInstance()

    fun getUserTasksCollection(userId: String) =
        db.collection("Users").document(userId).collection("Tasks")
}

