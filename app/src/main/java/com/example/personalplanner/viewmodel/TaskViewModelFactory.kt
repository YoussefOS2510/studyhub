// TaskViewModelFactory.kt
package com.example.personalplanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.personalplanner.data.remote.AuthRepository
import com.example.personalplanner.data.repository.TaskRepository

class TaskViewModelFactory(
    private val repository: TaskRepository,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TaskViewModel(repository, authRepository) as T
    }
}
