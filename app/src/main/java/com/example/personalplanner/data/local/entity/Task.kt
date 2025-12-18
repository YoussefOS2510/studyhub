package com.example.personalplanner.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val subject: String = "",
    val deadline: Long = 0L,
    val isFinished: Boolean = false,
    val logTime: Long = 0L,
    val subtasks: List<Subtask> = emptyList(),
    val userId: String = "",
    val updatedAt: Long = 0L
)
