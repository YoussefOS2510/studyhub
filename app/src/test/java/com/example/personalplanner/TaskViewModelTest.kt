package com.example.personalplanner.viewmodel

import com.example.personalplanner.MainDispatcherRule
import com.example.personalplanner.data.local.entity.Task
import com.example.personalplanner.data.remote.AuthRepository
import com.example.personalplanner.data.repository.TaskRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class TaskViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    lateinit var mockTaskRepository: TaskRepository

    @Mock
    lateinit var mockAuthRepository: AuthRepository

    @Mock
    lateinit var mockFirebaseUser: FirebaseUser

    lateinit var viewModel: TaskViewModel

    private val testUserUid = "test_user_123"
    private val task1 = Task(id = 1, title = "Math", userId = testUserUid)

    private val userFlow = MutableStateFlow<FirebaseUser?>(null)

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        `when`(mockFirebaseUser.uid).thenReturn(testUserUid)

        `when`(mockAuthRepository.currentUser).thenReturn(userFlow)

        `when`(mockTaskRepository.getUserTasks(testUserUid))
            .thenReturn(flowOf(listOf(task1)))

        userFlow.value = mockFirebaseUser

        viewModel = TaskViewModel(mockTaskRepository, mockAuthRepository)
    }

    @Test
    fun `allTasks emits tasks for current user`() = runTest {

        val result = viewModel.allTasks.first()

        assertEquals(1, result.size)
        assertEquals("Math", result[0].title)
    }

    @Test
    fun `insertTask calls repository addTask with correct user`() = runTest {
        viewModel.insertTask(task1)


        verify(mockTaskRepository).addTask(task1, mockFirebaseUser)
    }

    @Test
    fun `updateTask calls repository updateTask`() = runTest {
        val updatedTask = task1.copy(title = "Math Advanced")

        viewModel.updateTask(updatedTask)

        verify(mockTaskRepository).updateTask(updatedTask, mockFirebaseUser)
    }

    @Test
    fun `deleteTask calls repository deleteTask`() = runTest {
        viewModel.deleteTask(task1)

        verify(mockTaskRepository).deleteTask(task1, mockFirebaseUser)
    }

    @Test
    fun `syncFromCloud calls repository syncFromFirestore`() = runTest {
        viewModel.syncFromCloud()

        verify(mockTaskRepository).syncFromFirestore(mockFirebaseUser)
    }
}