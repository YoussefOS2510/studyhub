package com.example.studyplannerapp.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import android.app.DatePickerDialog
import androidx.compose.material.icons.filled.Menu
import java.util.*

@Composable
fun CreateNewTaskScreen(
    onCreate: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onCancel: () -> Unit = {}
) {
    val isDark = isSystemInDarkTheme()
    val borderColor = if (isDark) Color(0xFF555555) else Color(0xFFDDDDDD)
    val errorColor = Color(0xFFD32F2F)
    val mutedText = if (isDark) Color(0xFFAAAAAA) else Color(0xFF666666)
    val cardBg = if (isDark) Color(0xFF2A2A2A) else Color.White
    val backgroundColor = if (isDark) Color(0xFF121212) else Color(0xFFF8F9FA)

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("Math") }
    var expanded by remember { mutableStateOf(false) }
    var deadline by remember { mutableStateOf("") }

    // Validation states
    var showTitleError by remember { mutableStateOf(false) }
    var showDeadlineError by remember { mutableStateOf(false) }

    val subjectList = listOf("Math", "Science", "English", "History", "Other")
    val context = LocalContext.current

    // Date picker logic
    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            deadline = "${month + 1}/$dayOfMonth/$year"
            showDeadlineError = false // Clear error when date is selected
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    fun validateAndCreate() {
        var isValid = true

        // Validate title
        if (title.isBlank()) {
            showTitleError = true
            isValid = false
        } else {
            showTitleError = false
        }

        // Validate deadline
        if (deadline.isBlank()) {
            showDeadlineError = true
            isValid = false
        } else {
            showDeadlineError = false
        }

        // If valid, create task
        if (isValid) {
            onCreate(title, description, subject, deadline)
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Create new task",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 24.sp
                )
            }

            // Main Form
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardBg
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    //Title
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "Title ",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    1.dp,
                                    if (showTitleError) errorColor else borderColor,
                                    RoundedCornerShape(12.dp)
                                )
                                .background(cardBg)
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            BasicTextField(
                                value = title,
                                onValueChange = {
                                    title = it
                                    if (it.isNotBlank()) {
                                        showTitleError = false
                                    }
                                },
                                textStyle = TextStyle(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 16.sp
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                decorationBox = { innerTextField ->
                                    if (title.isEmpty()) {
                                        Text(
                                            "e.g., Complete Chapter 5 homework",
                                            color = mutedText,
                                            fontSize = 16.sp
                                        )
                                    }
                                    innerTextField()
                                },
                                singleLine = true
                            )
                        }

                        // Title error message
                        if (showTitleError) {
                            Text(
                                "Title is required",
                                color = errorColor,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }

                    //Description
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Description",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                .background(cardBg)
                                .padding(16.dp)
                        ) {
                            BasicTextField(
                                value = description,
                                onValueChange = { description = it },
                                textStyle = TextStyle(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 16.sp
                                ),
                                modifier = Modifier.fillMaxSize(),
                                decorationBox = { innerTextField ->
                                    if (description.isEmpty()) {
                                        Text(
                                            "Add more details about this task...",
                                            color = mutedText,
                                            fontSize = 16.sp
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }
                    }

                    //Subject
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Subject ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(cardBg)
                                    .clickable { expanded = true }
                                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        subject,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 16.sp
                                    )
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = mutedText
                                    )
                                }
                            }

                            //+
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                    .background(cardBg)
                                    .clickable { /* Add new subject logic */ },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add subject",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(cardBg)
                        ) {
                            subjectList.forEach { subjectItem ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            subjectItem,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 16.sp
                                        )
                                    },
                                    onClick = {
                                        subject = subjectItem
                                        expanded = false
                                    },
                                    modifier = Modifier.background(cardBg)
                                )
                            }
                        }
                    }

                    //Deadline
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "Deadline ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    1.dp,
                                    if (showDeadlineError) errorColor else borderColor,
                                    RoundedCornerShape(12.dp)
                                )
                                .background(cardBg)
                                .clickable { datePicker.show() }
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            Row(
                                verticalAlignment = CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.DateRange,
                                    contentDescription = null,
                                    tint = if (showDeadlineError) errorColor else mutedText,
                                    modifier = Modifier.size(20.dp)
                                )

                                if (deadline.isEmpty()) {
                                    Text(
                                        "mm/dd/yyyy",
                                        color = if (showDeadlineError) errorColor else mutedText,
                                        fontSize = 16.sp
                                    )
                                } else {
                                    Text(
                                        deadline,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }

                        // Deadline error message
                        if (showDeadlineError) {
                            Text(
                                "Deadline is required",
                                color = errorColor,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            //Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Create Task Button
                Button(
                    onClick = {
                        validateAndCreate()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 0.dp
                    )
                ) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Create Task",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Cancel Button
                OutlinedButton(
                    onClick = onCancel,
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(
                        "Cancel",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            //Tips
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) Color(0x1A4CAF50) else Color(0xFFE8F5E9)
                ),
                border = BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = if (isDark) 0.2f else 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF4CAF50)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "💡",
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        Text(
                            "Tips",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            fontSize = 18.sp
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(start = 44.dp)
                    ) {
                        Text(
                            "• Use descriptive titles to easily identify tasks",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                        Text(
                            "• Set realistic deadlines to stay organized",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                        Text(
                            "• Log study sessions to track your progress",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                        Text(
                            "• Click the + button to add custom subjects",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNewTaskLight() {
    MaterialTheme {
        CreateNewTaskScreen()
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewNewTaskDark() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        CreateNewTaskScreen()
    }
}