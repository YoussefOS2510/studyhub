package com.example.studyplannerapp.ui.screens

import androidx.compose.ui.tooling.preview.Preview
import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 1. IMPORT YOUR COLOR VARIABLES HERE
// If your Color.kt is in a different package, update this line.
// Example: import com.example.studyplannerapp.ui.theme.*
import com.example.studyplannerapp.ui.theme.* // --- Local Feature Colors ---
val StudyGreenLevel0 = Color(0xFF161B22)
val StudyGreenLevel1 = Color(0xFF0E4429)
val StudyGreenLevel2 = Color(0xFF006D32)
val StudyGreenLevel3 = Color(0xFF26A641)
val StudyGreenLevel4 = Color(0xFF39D353)
val StatusYellow = Color(0xFFD29922)

val DarkBorder = Color(color=0xFF717378)
val DarkMutedForeground= Color(color=0xFFFFFFFF)

val LightBorder = Color(color=0xFF717378)
val LightMutedForeground= Color(color=0xFF000000)
@Composable
fun StudyHubScreen() {
    // 2. DETECT THEME
    val isDark = isSystemInDarkTheme()

    // 3. MANUALLY RESOLVE MISSING THEME COLORS
    // Since MaterialTheme doesn't have 'border' or 'muted', we pick them here directly
    val borderColor = if(isDark) DarkBorder else LightBorder
    val mutedTextColor =  if(isDark) DarkMutedForeground else LightMutedForeground
    // We use 'surface' for card background, which maps to DarkCard/LightCard in your Theme.kt
    val cardBgColor = MaterialTheme.colorScheme.surface

    val tasks = remember { mutableStateListOf(
        Task(1, "Chapter", "Math", "Due in 1d", "#1")
    ) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            StudyActivityCard(borderColor, mutedTextColor, cardBgColor)
            Spacer(modifier = Modifier.height(24.dp))
            FilterSection(borderColor, mutedTextColor, cardBgColor)
            Spacer(modifier = Modifier.height(16.dp))

            if (tasks.isEmpty()) {
                EmptyStateView(borderColor, mutedTextColor, cardBgColor)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(tasks) { task ->
                        TaskItemCard(task, borderColor, mutedTextColor, cardBgColor)
                    }
                }
            }
        }
    }
}

// --- Sub-Composables ---

@Composable
fun StudyActivityCard(borderColor: Color, mutedText: Color, cardBg: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Study Activity",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "0 active days in the last 12 weeks",
                        style = MaterialTheme.typography.bodySmall,
                        color = mutedText
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = StudyGreenLevel4
                    )
                    Text(
                        text = "total minutes",
                        style = MaterialTheme.typography.bodySmall,
                        color = mutedText
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Heatmap
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(12) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        repeat(7) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(borderColor.copy(alpha = 0.3f))
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Less", fontSize = 10.sp, color = mutedText)
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(borderColor.copy(alpha = 0.3f)))
                        Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(StudyGreenLevel1))
                        Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(StudyGreenLevel2))
                        Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(StudyGreenLevel4))
                    }
                }
                Text("More", fontSize = 10.sp, color = mutedText)
            }
        }
    }
}

@Composable
fun FilterSection(borderColor: Color, mutedText: Color, cardBg: Color) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF238636),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.White))
                Spacer(modifier = Modifier.width(8.dp))
                Text("1 Open", fontSize = 14.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(mutedText))
                Spacer(modifier = Modifier.width(8.dp))
                Text("0 Closed", color = mutedText, fontSize = 14.sp)
            }
            Text("All 1", color = mutedText, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Search tasks...", color = mutedText, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = mutedText) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = borderColor,
                    unfocusedBorderColor = borderColor,
                    focusedContainerColor = cardBg,
                    unfocusedContainerColor = cardBg,
                    cursorColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f).height(50.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(50.dp)
            ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Deadline", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
fun TaskItemCard(task: Task, borderColor: Color, mutedText: Color, cardBg: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, StatusYellow),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = false,
                    onClick = {},
                    colors = RadioButtonDefaults.colors(unselectedColor = mutedText)
                )
                Text(
                    text = task.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        text = task.tag,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            Row(
                modifier = Modifier.padding(start = 40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.DateRange, contentDescription = null, tint = StatusYellow, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(task.dueText, color = StatusYellow, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Default.Share, contentDescription = null, tint = mutedText, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(task.branchId, color = mutedText, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = borderColor, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = {}) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = mutedText, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Log time", color = mutedText)
                }
                Row {
                    TextButton(onClick = {}) {
                        Icon(Icons.Outlined.Edit, contentDescription = null, tint = mutedText, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit", color = mutedText)
                    }
                    TextButton(onClick = {}) {
                        Icon(Icons.Outlined.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete", color = MaterialTheme.colorScheme.error)
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
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().height(200.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "No tasks found",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Create your first task to get started",
                fontSize = 14.sp,
                color = mutedText.copy(alpha = 0.7f)
            )
        }
    }
}

data class Task(
    val id: Int,
    val title: String,
    val tag: String,
    val dueText: String,
    val branchId: String
)

// --- Previews ---

@Preview(name = "Light Mode", showBackground = true)
@Composable
fun PreviewStudyHubLight() {
    // Replace 'MaterialTheme' with 'StudyplannerappTheme' if you want your custom typography
    MaterialTheme {
        StudyHubScreen()
    }
}

@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewStudyHubDark() {
    // We force a dark color scheme here so the preview simulates dark mode correctly
    MaterialTheme(colorScheme = darkColorScheme()) {
        StudyHubScreen()
    }
}