package com.example.personalplanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalplanner.data.local.entity.Task
import com.example.personalplanner.data.remote.AuthRepository
import com.example.personalplanner.data.repository.TaskRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi

import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class TaskViewModel(
    private val repository: TaskRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // Dynamic user
    private val currentUserFlow = authRepository.currentUser

    // Tasks flow
    @OptIn(ExperimentalCoroutinesApi::class)
    val allTasks = currentUserFlow.flatMapLatest { user ->
        repository.getUserTasks(user?.uid ?: "")
    }

    fun insertTask(task: Task) = viewModelScope.launch {
        currentUserFlow.value?.let { user ->
            repository.addTask(task, user)
        }
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        currentUserFlow.value?.let { user ->
            repository.updateTask(task, user)
        }
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        currentUserFlow.value?.let { user ->
            repository.deleteTask(task, user)
        }
    }

    fun clearAll() = viewModelScope.launch {
        currentUserFlow.value?.let { user ->
            repository.clearAll(user)
        }
    }

    fun syncFromCloud() = viewModelScope.launch {
        currentUserFlow.value?.let { user ->
            repository.syncFromFirestore(user)
        }
    }
}

