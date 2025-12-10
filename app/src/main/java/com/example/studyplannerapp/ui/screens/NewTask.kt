package com.example.studyplannerapp.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import android.app.DatePickerDialog
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Check
import com.example.studyplannerapp.data.local.entity.Task
import com.example.studyplannerapp.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CreateNewTaskScreen(
    taskId: String? = null,
    onCancel: () -> Unit = {},
    onTaskCreated: () -> Unit = {},  // Optional callback when task is successfully created
    viewModel: TaskViewModel,

) {
    val taskIdInt = taskId?.toIntOrNull()
    val allTasks by viewModel.allTasks.collectAsState(initial = emptyList())
    val existingTask = allTasks.find { it.id == taskIdInt }


    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("") }

    var showTitleError by remember { mutableStateOf(false) }
    var showSubjectError by remember { mutableStateOf(false) }
    var showDeadlineError by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

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
    LaunchedEffect(existingTask) {
        existingTask?.let {
            title = it.title
            description = it.description
            subject = it.subject
            deadline = SimpleDateFormat("M/d/yyyy", Locale.US).format(Date(it.deadline))
        }
    }

    fun saveTask() {
        showTitleError = title.isBlank()
        showSubjectError = subject.isBlank()
        showDeadlineError = deadline.isBlank()

        if (showTitleError || showSubjectError || showDeadlineError) return

        val formatter = SimpleDateFormat("M/d/yyyy", Locale.US)
        val deadlineTimestamp = formatter.parse(deadline)?.time ?: System.currentTimeMillis()

        if (existingTask != null) {
            // update
            val updatedTask = existingTask.copy(
                title = title.trim(),
                description = description.trim(),
                subject = subject.trim(),
                deadline = deadlineTimestamp
            )
            viewModel.updateTask(updatedTask)
        } else {
            // create new
            val newTask = Task(
                title = title.trim(),
                description = description.trim(),
                subject = subject.trim(),
                deadline = deadlineTimestamp,
                isFinished = false,
                logTime = 0L
            )
            viewModel.insertTask(newTask)
        }

        onTaskCreated()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Create new task",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Main Form Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // === Title Field ===
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row {
                            Text("Title", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(" *", color = Color(0xFFD32F2F))
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    1.dp,
                                    if (showTitleError) Color(0xFFD32F2F) else MaterialTheme.colorScheme.outline,
                                    RoundedCornerShape(12.dp)
                                )
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            BasicTextField(
                                value = title,
                                onValueChange = {
                                    title = it
                                    if (it.isNotBlank()) showTitleError = false
                                },
                                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                decorationBox = { innerTextField ->
                                    if (title.isEmpty()) {
                                        Text(
                                            "e.g., Complete Chapter 5 homework",
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }
                        if (showTitleError) {
                            Text("Title is required", color = Color(0xFFD32F2F), fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                        }
                    }

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
                                decorationBox = { innerTextField ->
                                    if (description.isEmpty()) {
                                        Text(
                                            "Add more details about this task...",
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }
                    }

                    // === Subject & Deadline Row ===
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Subject
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row {
                                Text("Subject", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text(" *", color = Color(0xFFD32F2F))
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(
                                        1.dp,
                                        if (showSubjectError) Color(0xFFD32F2F) else MaterialTheme.colorScheme.outline,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(horizontal = 16.dp, vertical = 14.dp)
                            ) {
                                BasicTextField(
                                    value = subject,
                                    onValueChange = {
                                        subject = it
                                        if (it.isNotBlank()) showSubjectError = false
                                    },
                                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp),
                                    singleLine = true,
                                    decorationBox = { innerTextField ->
                                        if (subject.isEmpty()) {
                                            Text("e.g., Math, Physics...", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                        }
                                        innerTextField()
                                    }
                                )
                            }
                            if (showSubjectError) {
                                Text("Subject is required", color = Color(0xFFD32F2F), fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                            }
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
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        Icons.Outlined.DateRange,
                                        contentDescription = null,
                                        tint = if (showDeadlineError) Color(0xFFD32F2F) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                    Text(
                                        text = if (deadline.isEmpty()) "mm/dd/yyyy" else deadline,
                                        color = if (deadline.isEmpty()) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                            if (showDeadlineError) {
                                Text("Deadline is required", color = Color(0xFFD32F2F), fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { saveTask() },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Create task", fontWeight = FontWeight.Medium)
                }

                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("Cancel", fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

