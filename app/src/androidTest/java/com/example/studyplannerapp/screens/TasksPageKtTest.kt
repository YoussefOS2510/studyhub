package com.example.studyplannerapp.screens

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.studyplannerapp.data.local.entity.Task
import com.example.studyplannerapp.ui.screens.StudyHubScreenContent
import org.junit.Rule
import org.junit.Test

class TasksPageKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // --- Mock Data ---
    private val openTask = Task(
        id = 1,
        title = "Math Homework",
        description = "Algebra 101",
        isFinished = false
    )

    private val closedTask = Task(
        id = 2,
        title = "History Essay",
        description = "World War II",
        isFinished = true
    )

    private val mixedList = listOf(openTask, closedTask)

    @Test
    fun studyHubScreen_displays_a_list_of_tasks() {
        composeTestRule.setContent {
            StudyHubScreenContent(
                allTasks = mixedList,
                onEditTask = {}, onDeleteTask = {}, onToggleTask = {}, onLogTime = { _, _ -> }
            )
        }

        // Verify items are displayed
        composeTestRule.onNodeWithText("Math Homework").assertIsDisplayed()
        composeTestRule.onNodeWithText("History Essay").assertIsDisplayed()

        // Verify Empty State is GONE
        composeTestRule.onNodeWithTag("empty_state").assertDoesNotExist()
    }

    @Test
    fun studyHubScreen_All_filter_functionality() {
        composeTestRule.setContent {
            StudyHubScreenContent(
                allTasks = mixedList,
                onEditTask = {}, onDeleteTask = {}, onToggleTask = {}, onLogTime = { _, _ -> }
            )
        }

        // Click 'All' (It's default, but let's be explicit)
        composeTestRule.onNodeWithTag("Filter_All").performClick()

        // Verify Both exist
        composeTestRule.onNodeWithText("Math Homework").assertIsDisplayed()
        composeTestRule.onNodeWithText("History Essay").assertIsDisplayed()
    }

    @Test
    fun studyHubScreen_Open_filter_functionality() {
        composeTestRule.setContent {
            StudyHubScreenContent(
                allTasks = mixedList,
                onEditTask = {}, onDeleteTask = {}, onToggleTask = {}, onLogTime = { _, _ -> }
            )
        }

        // Click 'Open' Filter
        composeTestRule.onNodeWithTag("Filter_Open").performClick()

        // Verify: Open is there, Closed is GONE
        composeTestRule.onNodeWithText("Math Homework").assertIsDisplayed()
        composeTestRule.onNodeWithText("History Essay").assertDoesNotExist()
    }

    @Test
    fun studyHubScreen_Closed_filter_functionality() {
        composeTestRule.setContent {
            StudyHubScreenContent(
                allTasks = mixedList,
                onEditTask = {}, onDeleteTask = {}, onToggleTask = {}, onLogTime = { _, _ -> }
            )
        }

        // Click 'Closed' Filter
        composeTestRule.onNodeWithTag("Filter_Closed").performClick()

        // Verify: Closed is there, Open is GONE
        composeTestRule.onNodeWithText("History Essay").assertIsDisplayed()
        composeTestRule.onNodeWithText("Math Homework").assertDoesNotExist()
    }

    @Test
    fun studyHubScreen_search_functionality_with_matching_results() {
        composeTestRule.setContent {
            StudyHubScreenContent(
                allTasks = mixedList,
                onEditTask = {}, onDeleteTask = {}, onToggleTask = {}, onLogTime = { _, _ -> }
            )
        }

        // Type "Math"
        composeTestRule.onNodeWithTag("search_bar").performTextInput("Math")

        // Verify results
        composeTestRule.onNodeWithText("Math Homework").assertIsDisplayed()
        composeTestRule.onNodeWithText("History Essay").assertDoesNotExist()
    }

    @Test
    fun studyHubScreen_search_with_no_matching_results() {
        composeTestRule.setContent {
            StudyHubScreenContent(
                allTasks = mixedList,
                onEditTask = {}, onDeleteTask = {}, onToggleTask = {}, onLogTime = { _, _ -> }
            )
        }

        // Type nonsense
        composeTestRule.onNodeWithTag("search_bar").performTextInput("Biology")

        // Verify nothing shown (Empty State likely appears)
        composeTestRule.onNodeWithText("Math Homework").assertDoesNotExist()
        composeTestRule.onNodeWithTag("empty_state").assertIsDisplayed()
    }

    @Test
    fun studyHubScreen_case_insensitive_search() {
        composeTestRule.setContent {
            StudyHubScreenContent(
                allTasks = mixedList,
                onEditTask = {}, onDeleteTask = {}, onToggleTask = {}, onLogTime = { _, _ -> }
            )
        }

        // Type "MATH" (Uppercase)
        composeTestRule.onNodeWithTag("search_bar").performTextInput("MATH")

        // Verify it still finds "Math Homework"
        composeTestRule.onNodeWithText("Math Homework").assertIsDisplayed()
    }

    @Test
    fun studyHubScreen_clear_search_query() {
        composeTestRule.setContent {
            StudyHubScreenContent(
                allTasks = mixedList,
                onEditTask = {}, onDeleteTask = {}, onToggleTask = {}, onLogTime = { _, _ -> }
            )
        }

        // 1. Filter by typing
        composeTestRule.onNodeWithTag("search_bar").performTextInput("Math")
        composeTestRule.onNodeWithText("History Essay").assertDoesNotExist()

        // 2. Clear text (Replace text with empty string)
        composeTestRule.onNodeWithTag("search_bar").performTextClearance()

        // 3. Verify original list is back
        composeTestRule.onNodeWithText("Math Homework").assertIsDisplayed()
        composeTestRule.onNodeWithText("History Essay").assertIsDisplayed()
    }

    @Test
    fun studyHubScreen_combined_filter_and_search() {
        composeTestRule.setContent {
            StudyHubScreenContent(
                allTasks = mixedList,
                onEditTask = {}, onDeleteTask = {}, onToggleTask = {}, onLogTime = { _, _ -> }
            )
        }

        // 1. Select 'Closed' (Should show History)
        composeTestRule.onNodeWithTag("Filter_Closed").performClick()

        // 2. Search for 'Math' (Should show nothing, because Math is Open)
        composeTestRule.onNodeWithTag("search_bar").performTextInput("Math")

        // Verify result is empty
        composeTestRule.onNodeWithText("Math Homework").assertDoesNotExist()
        composeTestRule.onNodeWithText("History Essay").assertDoesNotExist()
        composeTestRule.onNodeWithTag("empty_state").assertIsDisplayed()
    }

    @Test
    fun studyHubScreen_onEditTask_callback() {
        var clickedTask: Task? = null

        composeTestRule.setContent {
            StudyHubScreenContent(
                allTasks = listOf(openTask),
                onEditTask = { clickedTask = it }, // Capture the callback
                onDeleteTask = {}, onToggleTask = {}, onLogTime = { _, _ -> }
            )
        }

        // Click Edit Icon
        composeTestRule.onNodeWithTag("edit_button").performClick()

        // Verify we captured the right task
        assert(clickedTask == openTask)
    }

    @Test
    fun studyHubScreen_task_deletion_updates_UI() {
        // We must hold state locally to simulate UI updates in a stateless component test
        composeTestRule.setContent {
            // Create a mutable list specifically for this test
            val currentTasks = remember { mutableStateListOf(openTask, closedTask) }

            StudyHubScreenContent(
                allTasks = currentTasks,
                onDeleteTask = { taskToDelete ->
                    currentTasks.remove(taskToDelete) // Simulate DB deletion
                },
                onEditTask = {}, onToggleTask = {}, onLogTime = { _, _ -> }
            )
        }

        // 1. Verify initial state
        composeTestRule.onNodeWithText("Math Homework").assertIsDisplayed()

        // 2. Click Delete on Math Homework
        // Note: If you have multiple delete buttons, this might click the first one.
        // In a real app, use onNodeWithTag("delete_button_1") or similar unique ID.
        // For this list, we'll assume "Math" is first.
        composeTestRule.onAllNodesWithTag("delete_button").onFirst().performClick()

        // 3. Verify Math is GONE from UI
        composeTestRule.onNodeWithText("Math Homework").assertDoesNotExist()
        composeTestRule.onNodeWithText("History Essay").assertIsDisplayed()
    }

    @Test
    fun studyHubScreen_task_completion_updates_UI() {
        composeTestRule.setContent {
            val currentTasks = remember { mutableStateListOf(openTask) }

            StudyHubScreenContent(
                allTasks = currentTasks,
                onToggleTask = { taskToToggle ->
                    // Simulate updating the task status
                    val index = currentTasks.indexOf(taskToToggle)
                    if (index != -1) {
                        currentTasks[index] =
                            taskToToggle.copy(isFinished = !taskToToggle.isFinished)
                    }
                },
                onEditTask = {}, onDeleteTask = {}, onLogTime = { _, _ -> }
            )
            // Force Filter to 'Open' to visualize the task disappearing
        }

        // 1. Switch to 'Open' filter inside the UI
        composeTestRule.onNodeWithTag("Filter_Open").performClick()

        // 2. Verify Task is visible (it starts as Open)
        composeTestRule.onNodeWithText("Math Homework").assertIsDisplayed()

        // 3. Click Checkbox
        composeTestRule.onNodeWithTag("task_checkbox").performClick()

        // 4. Verify Task disappears (because it is now Closed, and we are on Open filter)
        composeTestRule.onNodeWithText("Math Homework").assertDoesNotExist()
    }
}