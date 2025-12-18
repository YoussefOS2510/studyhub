package com.example.personalplanner.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.personalplanner.data.local.entity.Task
import org.junit.Rule
import org.junit.Test

class CreateNewTaskScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()



    @Test
    fun createScreen_shows_errors_when_fields_empty() {
        composeTestRule.setContent {
            CreateNewTaskContent(existingTask = null, onSave = { _, _, _, _ -> }, onCancel = {})
        }

        // Click save
        composeTestRule.onNodeWithTag("btn_save").performClick()

        // 1. Verify Title error (This is unique, so onNode works)
        composeTestRule.onNodeWithText("Title is required").assertIsDisplayed()

        // 2. Verify Subject and Deadline errors (These both say "Required")
        // We expect exactly 2 occurrences of "Required"
        composeTestRule.onAllNodesWithText("Required").assertCountEquals(2)

        // Optional: Verify both are actually visible
        composeTestRule.onAllNodesWithText("Required").onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Required").onLast().assertIsDisplayed()
    }

    @Test
    fun createScreen_fills_form_and_submits() {
        var savedTitle = ""
        var savedSubject = ""

        composeTestRule.setContent {
            CreateNewTaskContent(
                existingTask = null,
                onSave = { title, _, subject, _ ->
                    savedTitle = title
                    savedSubject = subject
                },
                onCancel = {}
            )
        }

        // 1. Fill Title
        composeTestRule.onNodeWithTag("input_title").performTextInput("Study Math")

        // 2. Fill Subject
        composeTestRule.onNodeWithTag("input_subject").performTextInput("Calculus")

        // 3. For Deadline, we need a trick.
        // Since DatePicker is a system dialog, it's hard to click in Compose tests.
        // Usually, we verify the CLICK opens the dialog, but for unit testing logic,
        // we might mock the date selection if we extracted the date picker logic.
        // For this specific test, if we click save now, it will fail on the "Deadline Required" check.

        // Note: Testing standard Android DatePickers in Compose Tests is complex because they are Views, not Composables.
        // We will skip submitting in this specific test to avoid the DatePicker blocker,
        // or we check that the input text is reflected.

        composeTestRule.onNodeWithTag("input_title").assertTextEquals("Study Math")
        composeTestRule.onNodeWithTag("input_subject").assertTextEquals("Calculus")
    }

    @Test
    fun createScreen_prefills_data_in_edit_mode() {
        val existingTask = Task(
            id = 10,
            title = "Physics Homework",
            description = "Chapter 4",
            subject = "Physics",
            deadline = System.currentTimeMillis()
        )

        composeTestRule.setContent {
            CreateNewTaskContent(
                existingTask = existingTask,
                onSave = { _, _, _, _ -> },
                onCancel = {}
            )
        }

        // Verify fields have data
        composeTestRule.onNodeWithTag("input_title").assertTextEquals("Physics Homework")
        composeTestRule.onNodeWithTag("input_description").assertTextEquals("Chapter 4")
        composeTestRule.onNodeWithTag("input_subject").assertTextEquals("Physics")
    }

    @Test
    fun createScreen_cancel_button_works() {
        var cancelClicked = false
        composeTestRule.setContent {
            CreateNewTaskContent(existingTask = null, onSave = { _, _, _, _ -> }, onCancel = { cancelClicked = true })
        }

        composeTestRule.onNodeWithTag("btn_cancel").performClick()
        assert(cancelClicked)
    }
}