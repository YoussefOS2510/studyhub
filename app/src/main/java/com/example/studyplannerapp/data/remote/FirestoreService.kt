package com.example.studyplannerapp.data.remote

import com.google.firebase.firestore.FirebaseFirestore

class FirestoreService {
    private val db = FirebaseFirestore.getInstance()

    fun getUserTasksCollection(userId: String) =
        db.collection("users").document(userId).collection("tasks")
}

