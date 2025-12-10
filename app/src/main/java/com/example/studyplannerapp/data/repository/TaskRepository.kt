package com.example.studyplannerapp.data.repository


import com.example.studyplannerapp.data.local.dao.TaskDao
import com.example.studyplannerapp.data.local.entity.Task
import com.example.studyplannerapp.data.remote.FirestoreService
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class TaskRepository(
    private val dao: TaskDao,
    private val firestore: FirestoreService
) {

    fun getUserTasks(userId: String): Flow<List<Task>> =
        dao.getTasksForUser(userId)

    suspend fun addTask(task: Task, user: FirebaseUser?) {
        val userId = user?.uid ?: return
        val taskWithUser = task.copy(userId = userId)

        // 1) Save to room immediately
        dao.insert(taskWithUser)

        // 2) Sync to Firestore
        firestore.getUserTasksCollection(userId)
            .document(taskWithUser.id.toString())
            .set(taskWithUser)
            .await()
    }

    suspend fun updateTask(task: Task, user: FirebaseUser?) {
        val userId = user?.uid ?: return
        dao.update(task)

        firestore.getUserTasksCollection(userId)
            .document(task.id.toString())
            .set(task)
            .await()
    }

    suspend fun deleteTask(task: Task, user: FirebaseUser?) {
        val userId = user?.uid ?: return
        dao.delete(task)

        firestore.getUserTasksCollection(userId)
            .document(task.id.toString())
            .delete()
            .await()
    }

    suspend fun clearAll(user: FirebaseUser?) {
        val userId = user?.uid ?: return

        // Clear room
        dao.clearTasksForUser(userId)

        // Clear Firestore
        val collection = firestore.getUserTasksCollection(userId)
        val snapshot = collection.get().await()
        snapshot.documents.forEach { it.reference.delete() }
    }

    // Download Firestore → Room on login
    suspend fun syncFromFirestore(user: FirebaseUser?) = withContext(Dispatchers.IO) {
        val userId = user?.uid ?: return@withContext

        val snapshot = firestore.getUserTasksCollection(userId).get().await()

        val tasks = snapshot.documents.mapNotNull { it.toObject(Task::class.java) }

        dao.replaceAllForUser(userId, tasks)
    }
}
