package com.example.studyplannerapp.viewmodel

import com.example.studyplannerapp.MainDispatcherRule
import com.example.studyplannerapp.data.local.entity.Task
import com.example.studyplannerapp.data.remote.AuthRepository
import com.example.studyplannerapp.data.repository.TaskRepository
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

    // Mocks
    @Mock
    lateinit var mockTaskRepository: TaskRepository

    @Mock
    lateinit var mockAuthRepository: AuthRepository

    @Mock
    lateinit var mockFirebaseUser: FirebaseUser

    // The Class Under Test
    lateinit var viewModel: TaskViewModel

    // Test Data
    private val testUserUid = "test_user_123"
    private val task1 = Task(id = 1, title = "Math", userId = testUserUid)

    // We use a real MutableStateFlow to simulate the Auth Stream
    private val userFlow = MutableStateFlow<FirebaseUser?>(null)

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        // 1. Train the User Mock
        `when`(mockFirebaseUser.uid).thenReturn(testUserUid)

        // 2. Train the AuthRepo to return our controlling flow
        `when`(mockAuthRepository.currentUser).thenReturn(userFlow)

        // 3. Train the TaskRepo to return a flow of tasks when requested
        `when`(mockTaskRepository.getUserTasks(testUserUid))
            .thenReturn(flowOf(listOf(task1)))

        // 4. Set the initial state of the user flow to our mock user
        userFlow.value = mockFirebaseUser

        // 5. Initialize ViewModel
        viewModel = TaskViewModel(mockTaskRepository, mockAuthRepository)
    }

    @Test
    fun `allTasks emits tasks for current user`() = runTest {
        // Since we set userFlow.value = mockFirebaseUser in setup,
        // the flatMapLatest should trigger immediately.

        // Act: Collect the first emission from the ViewModel
        val result = viewModel.allTasks.first()

        // Assert
        assertEquals(1, result.size)
        assertEquals("Math", result[0].title)
    }

    @Test
    fun `insertTask calls repository addTask with correct user`() = runTest {
        // Act
        viewModel.insertTask(task1)

        // Assert
        // Verify that the repository's addTask was called with the specific task AND the mock user
        verify(mockTaskRepository).addTask(task1, mockFirebaseUser)
    }

    @Test
    fun `updateTask calls repository updateTask`() = runTest {
        val updatedTask = task1.copy(title = "Math Advanced")

        // Act
        viewModel.updateTask(updatedTask)

        // Assert
        verify(mockTaskRepository).updateTask(updatedTask, mockFirebaseUser)
    }

    @Test
    fun `deleteTask calls repository deleteTask`() = runTest {
        // Act
        viewModel.deleteTask(task1)

        // Assert
        verify(mockTaskRepository).deleteTask(task1, mockFirebaseUser)
    }

    @Test
    fun `syncFromCloud calls repository syncFromFirestore`() = runTest {
        // Act
        viewModel.syncFromCloud()

        // Assert
        verify(mockTaskRepository).syncFromFirestore(mockFirebaseUser)
    }
}