package com.example.personalplanner.data.repository

import com.example.personalplanner.data.local.dao.TaskDao
import com.example.personalplanner.data.local.entity.Task
import com.example.personalplanner.data.remote.FirestoreService
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

        val totalLogTime = task.subtasks.sumOf { it.logTime }
        val taskWithUser = task.copy(
            userId = userId,
            updatedAt = System.currentTimeMillis(),
            logTime = totalLogTime
        )

        val newId = dao.upsert(taskWithUser).toInt()
        val finalTask = taskWithUser.copy(id = newId)

        firestore.getUserTasksCollection(userId)
            .document(finalTask.id.toString())
            .set(finalTask)
            .await()
    }

    suspend fun updateTask(task: Task, user: FirebaseUser?) {
        val userId = user?.uid ?: return

        val totalLogTime = task.subtasks.sumOf { it.logTime }
        var taskToUpdate = task.copy(
            logTime = totalLogTime,
            updatedAt = System.currentTimeMillis()
        )

        // If the task has a default ID, it means we need to insert it and get the real ID.
        if (taskToUpdate.id == 0) {
            val newId = dao.upsert(taskToUpdate).toInt()
            taskToUpdate = taskToUpdate.copy(id = newId)
        } else {
            dao.upsert(taskToUpdate)
        }

        firestore.getUserTasksCollection(userId)
            .document(taskToUpdate.id.toString())
            .set(taskToUpdate)
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

        dao.clearTasksForUser(userId)

        val collection = firestore.getUserTasksCollection(userId)
        val snapshot = collection.get().await()
        snapshot.documents.forEach { it.reference.delete() }
    }

    suspend fun syncFromFirestore(user: FirebaseUser?) = withContext(Dispatchers.IO) {
        val userId = user?.uid ?: return@withContext
        val lastSync = dao.getLastUpdateTimestamp(userId) ?: 0L

        val snapshot = firestore.getUserTasksCollection(userId)
            .whereGreaterThan("updatedAt", lastSync)
            .get()
            .await()

        val remoteTasks = snapshot.documents.mapNotNull { it.toObject(Task::class.java) }

        if (remoteTasks.isNotEmpty()) {
            dao.upsertAll(remoteTasks)
        }
    }
}