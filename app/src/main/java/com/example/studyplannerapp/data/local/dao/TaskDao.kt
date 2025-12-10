package com.example.studyplannerapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.studyplannerapp.data.local.entity.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE userId = :userId")
    fun getTasksForUser(userId: String): Flow<List<Task>>

    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("DELETE FROM tasks WHERE userId = :userId")
    suspend fun clearTasksForUser(userId: String)

    @Query("DELETE FROM tasks WHERE userId = :userId")
    suspend fun deleteAllUserTasks(userId: String)

    @Insert
    suspend fun insertAll(tasks: List<Task>)

    @Transaction
    suspend fun replaceAllForUser(userId: String, tasks: List<Task>) {
        clearTasksForUser(userId)
        insertAll(tasks)
    }
}
