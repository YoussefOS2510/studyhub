package com.example.personalplanner.screens

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.personalplanner.data.local.entity.Task
import com.example.personalplanner.ui.screens.StudyHubScreenContent
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

        composeTestRule.onNodeWithText("Math Homework").assertIsDisplayed()
        composeTestRule.onNodeWithText("History Essay").assertIsDisplayed()

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

        composeTestRule.onNodeWithTag("Filter_All").performClick()

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

        composeTestRule.onNodeWithTag("Filter_Open").performClick()

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

        composeTestRule.onNodeWithTag("Filter_Closed").performClick()

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

        composeTestRule.onNodeWithTag("search_bar").performTextInput("Math")

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

        composeTestRule.onNodeWithTag("search_bar").performTextInput("Biology")

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

        composeTestRule.onNodeWithTag("search_bar").performTextInput("MATH")

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

        composeTestRule.onNodeWithTag("search_bar").performTextInput("Math")
        composeTestRule.onNodeWithText("History Essay").assertDoesNotExist()

        composeTestRule.onNodeWithTag("search_bar").performTextClearance()

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

        composeTestRule.onNodeWithTag("Filter_Closed").performClick()

        composeTestRule.onNodeWithTag("search_bar").performTextInput("Math")

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
                onEditTask = { clickedTask = it },
                onDeleteTask = {}, onToggleTask = {}, onLogTime = { _, _ -> }
            )
        }

        composeTestRule.onNodeWithTag("edit_button").performClick()

        assert(clickedTask == openTask)
    }

    @Test
    fun studyHubScreen_task_deletion_updates_UI() {
        composeTestRule.setContent {

            val currentTasks = remember { mutableStateListOf(openTask, closedTask) }

            StudyHubScreenContent(
                allTasks = currentTasks,
                onDeleteTask = { taskToDelete ->
                    currentTasks.remove(taskToDelete)
                },
                onEditTask = {}, onToggleTask = {}, onLogTime = { _, _ -> }
            )
        }

        composeTestRule.onNodeWithText("Math Homework").assertIsDisplayed()


        composeTestRule.onAllNodesWithTag("delete_button").onFirst().performClick()

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
                    val index = currentTasks.indexOf(taskToToggle)
                    if (index != -1) {
                        currentTasks[index] =
                            taskToToggle.copy(isFinished = !taskToToggle.isFinished)
                    }
                },
                onEditTask = {}, onDeleteTask = {}, onLogTime = { _, _ -> }
            )
        }

        composeTestRule.onNodeWithTag("Filter_Open").performClick()

        composeTestRule.onNodeWithText("Math Homework").assertIsDisplayed()

        composeTestRule.onNodeWithTag("task_checkbox").performClick()

        composeTestRule.onNodeWithText("Math Homework").assertDoesNotExist()
    }
}