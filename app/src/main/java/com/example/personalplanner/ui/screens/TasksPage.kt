package com.example.personalplanner.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Timer
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
import androidx.compose.ui.window.Dialog
import com.example.personalplanner.data.local.entity.Task
import com.example.personalplanner.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.graphics.Color
import com.example.personalplanner.data.local.entity.Subtask

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
val DeleteRed = Color(0xFFFF5252)
val EditBlue = Color(0xFF448AFF)

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
        onLogTime = { t, m -> viewModel.updateTask(t.copy(logTime = t.logTime + m)) },
        onUpdateTask = { viewModel.updateTask(it) }
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
    onLogTime: (Task, Int) -> Unit,
    onUpdateTask: (Task) -> Unit
) {
    // Search & Filter states (Local UI state is fine here for testing interactions)
    var searchQuery by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf("All") } // All, Open, Closed
    var selectedTaskId by remember { mutableStateOf<Int?>(null) }
    
    val selectedTask = remember(allTasks, selectedTaskId) { 
        allTasks.find { it.id == selectedTaskId } 
    }

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

    if (selectedTask != null) {
        TaskDetailDialog(
            task = selectedTask,
            onDismiss = { selectedTaskId = null },
            onUpdateTask = onUpdateTask,
            dateFormat = dateFormat,
            borderColor = borderColor
        )
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
                        onEdit = onEditTask,
                        onClick = { selectedTaskId = task.id }
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
    onEdit: (Task) -> Unit,
    onClick: () -> Unit
) {
    var showLogInput by remember { mutableStateOf(false) }
    var minutesText by remember { mutableStateOf("") }

    val deadlineDate = remember(task.deadline) { Date(task.deadline) }
    val dueText = remember { dateFormat.format(deadlineDate) }

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, if (task.isFinished) StudyGreenLevel4 else borderColor),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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
                // Removed Subject and LogTime text from here as requested
            }

            Spacer(Modifier.height(12.dp))
            Divider(color = borderColor.copy(0.3f))
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Side: Subject
                Text(
                    text = task.subject,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )

                // Right Side: Log Time, Edit, Delete
                if (showLogInput) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = minutesText,
                            onValueChange = { if (it.all { c -> c.isDigit() }) minutesText = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.width(60.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = StudyGreenLevel4)
                        )
                        Spacer(Modifier.width(4.dp))
                        IconButton(onClick = {
                            val mins = minutesText.toIntOrNull() ?: 0
                            if (mins > 0) onLogTime(task, mins)
                            showLogInput = false
                            minutesText = ""
                        }) { Icon(Icons.Default.Check, null, tint = StudyGreenLevel4) }
                        IconButton(onClick = { showLogInput = false; minutesText = "" }) {
                            Icon(Icons.Default.Close, null, tint = mutedText)
                        }
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { showLogInput = true }) {
                            Icon(Icons.Outlined.Timer, null, tint = StatusYellow, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(if (task.logTime > 0) "${task.logTime}m" else "Log", color = StatusYellow, fontSize = 14.sp)
                        }

                        IconButton(
                            onClick = { onEdit(task) },
                            modifier = Modifier.testTag("edit_button") // TAG FOR TESTING
                        ) {
                            Icon(Icons.Outlined.Edit, null, tint = EditBlue)
                        }
                        IconButton(
                            onClick = { onDelete(task) },
                            modifier = Modifier.testTag("delete_button") // TAG FOR TESTING
                        ) {
                            Icon(Icons.Outlined.Delete, null, tint = DeleteRed)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskDetailDialog(
    task: Task,
    onDismiss: () -> Unit,
    onUpdateTask: (Task) -> Unit,
    dateFormat: SimpleDateFormat,
    borderColor: Color
) {
    val deadlineDate = remember(task.deadline) { Date(task.deadline) }
    val dueText = remember { dateFormat.format(deadlineDate) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 700.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                // Header
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.DateRange, contentDescription = null, tint = StatusYellow, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(dueText, color = StatusYellow, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.width(16.dp))
                    Text(task.subject, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))

                // Description
                if (task.description.isNotBlank()) {
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Subtasks
                if (task.subtasks.isNotEmpty()) {
                    Text(
                        text = "Subtasks",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyColumn(
                        modifier = Modifier.weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(task.subtasks) { index, subtask ->
                            SubtaskDetailItem(
                                subtask = subtask,
                                borderColor = borderColor,
                                onToggle = { isChecked ->
                                    val newSubtasks = task.subtasks.toMutableList()
                                    newSubtasks[index] = subtask.copy(isFinished = isChecked)
                                    onUpdateTask(task.copy(subtasks = newSubtasks))
                                },
                                onLogTime = { minutes ->
                                    val newSubtasks = task.subtasks.toMutableList()
                                    newSubtasks[index] = subtask.copy(logTime = subtask.logTime + minutes)
                                    onUpdateTask(task.copy(subtasks = newSubtasks))
                                },
                                onEdit = { newTitle ->
                                    val newSubtasks = task.subtasks.toMutableList()
                                    newSubtasks[index] = subtask.copy(title = newTitle)
                                    onUpdateTask(task.copy(subtasks = newSubtasks))
                                },
                                onDelete = {
                                    val newSubtasks = task.subtasks.toMutableList()
                                    newSubtasks.removeAt(index)
                                    onUpdateTask(task.copy(subtasks = newSubtasks))
                                }
                            )
                        }
                    }
                } else {
                    Text(
                        text = "No subtasks",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
fun SubtaskDetailItem(
    subtask: Subtask,
    borderColor: Color,
    onToggle: (Boolean) -> Unit,
    onLogTime: (Long) -> Unit,
    onEdit: (String) -> Unit,
    onDelete: () -> Unit
) {
    var showLogDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    // Log Time Dialog
    if (showLogDialog) {
        var timeInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showLogDialog = false },
            title = { Text("Log Time (minutes)") },
            text = {
                OutlinedTextField(
                    value = timeInput,
                    onValueChange = { if (it.all { c -> c.isDigit() }) timeInput = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val minutes = timeInput.toLongOrNull() ?: 0L
                    if (minutes > 0) onLogTime(minutes)
                    showLogDialog = false
                }) { Text("Log") }
            },
            dismissButton = {
                TextButton(onClick = { showLogDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Edit Title Dialog
    if (showEditDialog) {
        var titleInput by remember { mutableStateOf(subtask.title) }
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Subtask") },
            text = {
                OutlinedTextField(
                    value = titleInput,
                    onValueChange = { titleInput = it },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (titleInput.isNotBlank()) onEdit(titleInput)
                    showEditDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Cancel") }
            }
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (subtask.isFinished) StudyGreenLevel4 else borderColor, RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Checkbox(
            checked = subtask.isFinished,
            onCheckedChange = onToggle,
            colors = CheckboxDefaults.colors(checkedColor = StudyGreenLevel4)
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = subtask.title,
                style = MaterialTheme.typography.bodyMedium,
                textDecoration = if (subtask.isFinished) TextDecoration.LineThrough else null,
                fontWeight = FontWeight.Medium
            )
            if (subtask.logTime > 0) {
                Text(
                    text = "${subtask.logTime}m logged",
                    style = MaterialTheme.typography.bodySmall,
                    color = StudyGreenLevel4
                )
            }
        }
        
        // Actions
        Row {
            IconButton(onClick = { showLogDialog = true }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Outlined.Timer, contentDescription = "Log Time", modifier = Modifier.size(18.dp))
            }
            IconButton(onClick = { showEditDialog = true }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Outlined.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp), tint = EditBlue)
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = DeleteRed, modifier = Modifier.size(18.dp))
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