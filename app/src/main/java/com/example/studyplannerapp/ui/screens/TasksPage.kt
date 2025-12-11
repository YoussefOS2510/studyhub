package com.example.studyplannerapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag // Required for testing
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyplannerapp.data.local.entity.Task
import com.example.studyplannerapp.ui.theme.* import com.example.studyplannerapp.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.graphics.Color

// Your custom green palette (Ensure these exist in your theme or keep them here)
val StudyGreenLevel0 = Color(0xFF161B22)
val StudyGreenLevel1 = Color(0xFF0E4429)
val StudyGreenLevel2 = Color(0xFF006D32)
val StudyGreenLevel3 = Color(0xFF26A641)
val StudyGreenLevel4 = Color(0xFF39D353)
val StatusYellow = Color(0xFFD29922)

val DarkBorder = Color(0xFF717378)
val DarkMutedForeground = Color(0xFFCCCCCC)
val LightBorder = Color(0xFF717378)
val LightMutedForeground = Color(0xFF666666)

// ---------------------------------------------------------
// 1. The Stateful Component (Connects to ViewModel)
// ---------------------------------------------------------
@Composable
fun StudyHubScreen(
    onEditTask: (Task) -> Unit = {},
    viewModel: TaskViewModel
) {
    val allTasks by viewModel.allTasks.collectAsState(initial = emptyList())

    // Pass the raw data to the stateless content.
    // The Content component will handle the visual filtering/searching.
    StudyHubScreenContent(
        allTasks = allTasks,
        onEditTask = onEditTask,
        onDeleteTask = { viewModel.deleteTask(it) },
        onToggleTask = { viewModel.updateTask(it.copy(isFinished = !it.isFinished)) },
        onLogTime = { t, m -> viewModel.updateTask(t.copy(logTime = t.logTime + m)) }
    )
}

// ---------------------------------------------------------
// 2. The Stateless Component (THIS IS WHAT YOU TEST)
// ---------------------------------------------------------
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun StudyHubScreenContent(
    allTasks: List<Task>,
    onEditTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onToggleTask: (Task) -> Unit,
    onLogTime: (Task, Int) -> Unit
) {
    // Search & Filter states (Local UI state is fine here for testing interactions)
    var searchQuery by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf("All") } // All, Open, Closed

    // Colors
    val isDark = isSystemInDarkTheme()
    val borderColor = if (isDark) DarkBorder else LightBorder
    val mutedTextColor = if (isDark) DarkMutedForeground else LightMutedForeground
    val cardBgColor = MaterialTheme.colorScheme.surface

    val dateFormat = remember { SimpleDateFormat("MMM dd", Locale.getDefault()) }

    // 🔍 APPLY SEARCH + FILTER LOGIC (Inside the UI component so we can test it)
    val displayedTasks = allTasks
        .filter { task ->
            when (filterType) {
                "Open" -> !task.isFinished
                "Closed" -> task.isFinished
                else -> true
            }
        }
        .filter { task ->
            searchQuery.isBlank() ||
                    task.title.contains(searchQuery, ignoreCase = true) ||
                    task.description.contains(searchQuery, ignoreCase = true)
        }

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .testTag("task_list"), // TAG FOR TESTING
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                FilterSection(
                    filterType = filterType,
                    onFilterChange = { filterType = it },
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    borderColor = borderColor,
                    mutedText = mutedTextColor,
                    cardBg = cardBgColor
                )
            }

            if (displayedTasks.isEmpty()) {
                item {
                    Box(modifier = Modifier.testTag("empty_state")) { // TAG FOR TESTING
                        EmptyStateView(borderColor, mutedTextColor, cardBgColor)
                    }
                }
            } else {
                items(displayedTasks, key = { it.id }) { task ->
                    TaskItemCard(
                        task = task,
                        borderColor = borderColor,
                        mutedText = mutedTextColor,
                        cardBg = cardBgColor,
                        dateFormat = dateFormat,
                        onTaskToggle = onToggleTask,
                        onLogTime = onLogTime,
                        onDelete = onDeleteTask,
                        onEdit = onEditTask
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------
// 3. Sub-Composables
// ---------------------------------------------------------

@Composable
fun FilterSection(
    filterType: String,
    onFilterChange: (String) -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    borderColor: Color,
    mutedText: Color,
    cardBg: Color
) {
    Column {

        // 🔥 FILTER BUTTONS
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            listOf("All", "Open", "Closed").forEach { label ->
                val isSelected = filterType == label
                Button(
                    onClick = { onFilterChange(label) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) StudyGreenLevel3 else Color.Transparent,
                        contentColor = if (isSelected) Color.White else mutedText
                    ),
                    border = BorderStroke(1.dp, borderColor),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("Filter_$label") // TAG: Filter_All, Filter_Open, etc.
                ) {
                    Text(label, fontSize = 14.sp)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // 🔎 SEARCH BOX
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { onSearchChange(it) },
            placeholder = { Text("Search tasks...", color = mutedText) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = mutedText) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = borderColor,
                unfocusedBorderColor = borderColor
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(cardBg)
                .testTag("search_bar") // TAG FOR TESTING
        )
    }
}


@Composable
fun TaskItemCard(
    task: Task,
    borderColor: Color,
    mutedText: Color,
    cardBg: Color,
    dateFormat: SimpleDateFormat,
    onTaskToggle: (Task) -> Unit,
    onLogTime: (Task, Int) -> Unit,
    onDelete: (Task) -> Unit,
    onEdit: (Task) -> Unit
) {
    var showLogInput by remember { mutableStateOf(false) }
    var minutesText by remember { mutableStateOf("") }

    val deadlineDate = remember(task.deadline) { Date(task.deadline) }
    val dueText = remember { dateFormat.format(deadlineDate) }

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, if (task.isFinished) StudyGreenLevel4 else StatusYellow),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = task.isFinished,
                    onCheckedChange = { onTaskToggle(task) },
                    colors = CheckboxDefaults.colors(checkedColor = StudyGreenLevel4),
                    modifier = Modifier.testTag("task_checkbox") // TAG FOR TESTING
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (task.isFinished) 0.6f else 1f),
                        textDecoration = if (task.isFinished) TextDecoration.LineThrough else null
                    )
                    if (task.description.isNotBlank()) {
                        Text(task.description, fontSize = 13.sp, color = mutedText, maxLines = 2)
                    }
                }
            }

            Row(modifier = Modifier.padding(start = 40.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.DateRange, null, tint = StatusYellow, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(dueText, color = StatusYellow, fontSize = 12.sp)
                Spacer(Modifier.width(16.dp))
                Text(task.subject, color = mutedText, fontSize = 12.sp)
                Spacer(Modifier.width(16.dp))
                if (task.logTime > 0) {
                    Text("${task.logTime}m logged", color = StudyGreenLevel4, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(12.dp))
            Divider(color = borderColor.copy(0.3f))
            Spacer(Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                if (showLogInput) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = minutesText,
                            onValueChange = { if (it.all { c -> c.isDigit() }) minutesText = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.width(90.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = StudyGreenLevel4)
                        )
                        Spacer(Modifier.width(8.dp))
                        TextButton(onClick = {
                            val mins = minutesText.toIntOrNull() ?: 0
                            if (mins > 0) onLogTime(task, mins)
                            showLogInput = false
                            minutesText = ""
                        }) { Text("Log", color = StudyGreenLevel4) }
                        TextButton(onClick = { showLogInput = false; minutesText = "" }) {
                            Text("Cancel", color = mutedText)
                        }
                    }
                } else {
                    TextButton(onClick = { showLogInput = true }) {
                        Icon(Icons.Default.Add, null, tint = mutedText, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Log time", color = mutedText)
                    }
                }

                Row {
                    IconButton(
                        onClick = { onEdit(task) },
                        modifier = Modifier.testTag("edit_button") // TAG FOR TESTING
                    ) {
                        Icon(Icons.Outlined.Edit, null, tint = mutedText)
                    }
                    IconButton(
                        onClick = { onDelete(task) },
                        modifier = Modifier.testTag("delete_button") // TAG FOR TESTING
                    ) {
                        Icon(Icons.Outlined.Delete, null, tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(borderColor: Color, mutedText: Color, cardBg: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth().height(200.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon(Icons.Default.LibraryBooks, null, modifier = Modifier.size(48.dp), tint = mutedText.copy(0.6f))
            Spacer(Modifier.height(16.dp))
            Text("No tasks yet", fontWeight = FontWeight.Medium, fontSize = 18.sp)
            Text("Tap + to create your first task", color = mutedText)
        }
    }
}