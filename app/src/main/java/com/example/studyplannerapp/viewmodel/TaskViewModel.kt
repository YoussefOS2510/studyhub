package com.example.studyplannerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyplannerapp.data.local.dao.TaskDao
import com.example.studyplannerapp.data.local.entity.Task
import com.example.studyplannerapp.data.remote.AuthRepository
import com.example.studyplannerapp.data.repository.TaskRepository
import com.google.firebase.auth.FirebaseAuth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class TaskViewModel(
    private val repository: TaskRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val user = authRepository.currentUser.value

    val allTasks = repository.getUserTasks(user?.uid ?: "")

    fun insertTask(task: Task) = viewModelScope.launch {
        repository.addTask(task, user)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        repository.updateTask(task, user)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.deleteTask(task, user)
    }


    fun clearAll() = viewModelScope.launch {
        repository.clearAll(user)
    }

    fun syncFromCloud() = viewModelScope.launch {
        repository.syncFromFirestore(user)
    }
}
