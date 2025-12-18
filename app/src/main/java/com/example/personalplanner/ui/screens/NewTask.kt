package com.example.personalplanner.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag // Required for testing
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.personalplanner.data.local.entity.Subtask
import com.example.personalplanner.data.local.entity.Task
import com.example.personalplanner.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

// ---------------------------------------------------------
// 1. The Connector (Connects to ViewModel)
// ---------------------------------------------------------
@Composable
fun CreateNewTaskScreen(
    taskId: String? = null,
    onCancel: () -> Unit = {},
    onTaskCreated: () -> Unit = {},
    viewModel: TaskViewModel
) {
    val taskIdInt = taskId?.toIntOrNull()
    val allTasks by viewModel.allTasks.collectAsState(initial = emptyList())
    val existingTask = allTasks.find { it.id == taskIdInt }

    // Logic to handle saving (Extracting this logic keeps the UI clean)
    fun handleSave(title: String, desc: String, subject: String, deadline: Long, subtasks: List<Subtask>) {
        if (existingTask != null) {
            val updatedTask = existingTask.copy(
                title = title.trim(),
                description = desc.trim(),
                subject = subject.trim(),
                deadline = deadline,
                subtasks = subtasks
            )
            viewModel.updateTask(updatedTask)
        } else {
            val newTask = Task(
                title = title.trim(),
                description = desc.trim(),
                subject = subject.trim(),
                deadline = deadline,
                isFinished = false,
                logTime = 0L,
                subtasks = subtasks
            )
            viewModel.insertTask(newTask)
        }
        onTaskCreated()
    }

    CreateNewTaskContent(
        existingTask = existingTask,
        onSave = ::handleSave,
        onCancel = onCancel
    )
}

// ---------------------------------------------------------
// 2. The UI (This is what we TEST)
// ---------------------------------------------------------
@Composable
fun CreateNewTaskContent(
    existingTask: Task?,
    onSave: (title: String, desc: String, subject: String, deadline: Long, subtasks: List<Subtask>) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("") }
    var subtasks by remember { mutableStateOf(mutableListOf<Subtask>()) }

    var showTitleError by remember { mutableStateOf(false) }
    var showSubjectError by remember { mutableStateOf(false) }
    var showDeadlineError by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Pre-fill data if editing
    LaunchedEffect(existingTask) {
        existingTask?.let {
            title = it.title
            description = it.description
            subject = it.subject
            deadline = SimpleDateFormat("M/d/yyyy", Locale.US).format(Date(it.deadline))
            subtasks = it.subtasks.toMutableList()
        }
    }

    val datePicker = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                deadline = "${month + 1}/$dayOfMonth/$year"
                showDeadlineError = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    fun validateAndSave() {
        showTitleError = title.isBlank()
        showSubjectError = subject.isBlank()
        showDeadlineError = deadline.isBlank()

        if (showTitleError || showSubjectError || showDeadlineError) return

        val formatter = SimpleDateFormat("M/d/yyyy", Locale.US)
        val deadlineTimestamp = formatter.parse(deadline)?.time ?: System.currentTimeMillis()

        onSave(title, description, subject, deadlineTimestamp, subtasks)
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if(existingTask != null) "Edit task" else "Create new task",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // === Title Field ===
                    InputFieldWithLabel(
                        label = "Title",
                        value = title,
                        onValueChange = { title = it; if(it.isNotBlank()) showTitleError = false },
                        placeholder = "e.g., Complete Chapter 5 homework",
                        isError = showTitleError,
                        errorMsg = "Title is required",
                        testTag = "input_title" // TAG ADDED
                    )

                    // === Description ===
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Description", fontSize = 14.sp)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(16.dp)
                        ) {
                            BasicTextField(
                                value = description,
                                onValueChange = { description = it },
                                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp),
                                modifier = Modifier.testTag("input_description"), // TAG ADDED
                                decorationBox = { innerTextField ->
                                    if (description.isEmpty()) {
                                        Text("Add more details...", color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
                                    }
                                    innerTextField()
                                }
                            )
                        }
                    }

                    // === Subject & Deadline Row ===
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            InputFieldWithLabel(
                                label = "Subject",
                                value = subject,
                                onValueChange = { subject = it; if(it.isNotBlank()) showSubjectError = false },
                                placeholder = "Math",
                                isError = showSubjectError,
                                errorMsg = "Required",
                                testTag = "input_subject" // TAG ADDED
                            )
                        }

                        // Deadline
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row {
                                Text("Deadline", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text(" *", color = Color(0xFFD32F2F))
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(
                                        1.dp,
                                        if (showDeadlineError) Color(0xFFD32F2F) else MaterialTheme.colorScheme.outline,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .background(MaterialTheme.colorScheme.surface)
                                    .clickable { datePicker.show() }
                                    .padding(horizontal = 16.dp, vertical = 14.dp)
                                    .testTag("input_deadline") // TAG ADDED
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.DateRange, null, tint = if (showDeadlineError) Color(0xFFD32F2F) else MaterialTheme.colorScheme.onSurface.copy(0.5f))
                                    Spacer(Modifier.width(8.dp))
                                    Text(if (deadline.isEmpty()) "mm/dd/yyyy" else deadline, fontSize = 16.sp)
                                }
                            }
                            if (showDeadlineError) {
                                Text("Required", color = Color(0xFFD32F2F), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Subtasks Section
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Subtasks",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    subtasks.forEachIndexed { index, subtask ->
                        SubtaskItem(
                            subtask = subtask,
                            onUpdate = { updatedSubtask ->
                                val newList = subtasks.toMutableList()
                                newList[index] = updatedSubtask
                                subtasks = newList
                            },
                            onDelete = {
                                val newList = subtasks.toMutableList()
                                newList.removeAt(index)
                                subtasks = newList
                            }
                        )
                    }

                    Button(
                        onClick = {
                            subtasks = (subtasks + Subtask(title = "", isFinished = false, logTime = 0L)).toMutableList()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Add Subtask")
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Buttons
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { validateAndSave() },
                    modifier = Modifier.weight(1f).height(56.dp).testTag("btn_save"), // TAG ADDED
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Icon(Icons.Default.Check, null)
                    Spacer(Modifier.width(8.dp))
                    Text(if(existingTask != null) "Update task" else "Create task")
                }

                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f).height(56.dp).testTag("btn_cancel"), // TAG ADDED
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("Cancel")
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun SubtaskItem(
    subtask: Subtask,
    onUpdate: (Subtask) -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = subtask.title,
                onValueChange = { onUpdate(subtask.copy(title = it)) },
                textStyle = TextStyle(fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface),
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    if (subtask.title.isEmpty()) {
                        Text("Subtask title", color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
                    }
                    innerTextField()
                }
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete subtask", tint = Color.Red)
            }
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Checkbox(
                checked = subtask.isFinished,
                onCheckedChange = { onUpdate(subtask.copy(isFinished = it)) }
            )
            Text("Finished")
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text("Time (min): ")
            BasicTextField(
                value = if(subtask.logTime == 0L) "" else subtask.logTime.toString(),
                onValueChange = { 
                    if (it.isEmpty()) {
                        onUpdate(subtask.copy(logTime = 0L))
                    } else if (it.all { char -> char.isDigit() }) {
                        onUpdate(subtask.copy(logTime = it.toLong())) 
                    }
                },
                textStyle = TextStyle(fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface),
                modifier = Modifier.width(60.dp).border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp)).padding(4.dp),
                singleLine = true
            )
        }
    }
}


// Helper Composable to reduce code duplication
@Composable
fun InputFieldWithLabel(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean,
    errorMsg: String,
    testTag: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row {
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(" *", color = Color(0xFFD32F2F))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, if (isError) Color(0xFFD32F2F) else MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag(testTag), // TAG APPLIED HERE
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(placeholder, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                    innerTextField()
                }
            )
        }
        if (isError) {
            Text(errorMsg, color = Color(0xFFD32F2F), fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
        }
    }
}