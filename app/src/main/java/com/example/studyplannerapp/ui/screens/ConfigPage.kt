package com.example.studyplannerapp.ui.screens

// 1. ADD THIS IMPORT (REMOVED: android.R as it conflicts with R.string)
import androidx.compose.foundation.border

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope // **ADDED**
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyplannerapp.data.remote.AuthRepository

// Import your theme colors
import com.example.studyplannerapp.data.prefrences.ThemePreferenceManager // **ADDED**
import com.example.studyplannerapp.data.remote.UserProfile
import com.example.studyplannerapp.viewmodel.TaskViewModel
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch


@Composable
fun SettingsScreen(
    authRepository: AuthRepository,
    themeManager: ThemePreferenceManager,
    isDark: Boolean,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel
) {
    val scope = rememberCoroutineScope()
    val userProfile by authRepository.userProfile.collectAsState()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // === Profile Header ===
            ProfileHeader(userProfile = userProfile, onSignOut = onSignOut)

            // === Stats Section ===
            StatsSection(viewModel)

            // === Appearance ===
            AppearanceSection(
                isDark = isDark,
                onThemeChange = { dark ->
                    scope.launch { themeManager.setDarkThemeEnabled(dark) }
                }
            )

            // === Data Management ===
            DataManagementSection(viewModel)

            Spacer(Modifier.height(20.dp))
        }
    }
}


// SettingsScreen.kt (only the ProfileHeader part changed)
@Composable
private fun ProfileHeader(
    userProfile: UserProfile?,
    onSignOut: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar (use photoUrl if available, fallback to initials)
            val initials = userProfile?.displayName?.take(2)?.uppercase() ?: "DU"
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4285F4)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = userProfile?.displayName ?: "Guest User",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp
                )
                Text(
                    text = userProfile?.email ?: "guest@example.com",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
                OutlinedButton(
                    onClick = onSignOut,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Sign out", fontSize = 14.sp)
                }
            }
        }


    }
}
@Composable
private fun StatsSection( viewModel: TaskViewModel) {
    val tasks by viewModel.allTasks.collectAsState(initial = emptyList())

    val totalIssues = tasks.size
    val resolvedIssues = tasks.count { it.isFinished }
    val totalMinutes = tasks.sumOf { it.logTime ?: 0 } // Use your field OR replace with 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(">", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.width(8.dp))
            Text("Repository Stats", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }

        Spacer(Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatBox(value =totalIssues.toString(), label = "Issues", color = Color(0xFF42A5F5), modifier = Modifier.weight(1f))
            StatBox(value = resolvedIssues.toString(), label = "Resolved", color = Color(0xFF66BB6A), modifier = Modifier.weight(1f))
            StatBox(value = totalMinutes.toString(), label = "Minutes", color = Color(0xFFAB47BC), modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatBox(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(vertical = 20.dp)
    ) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 28.sp, color = color)
        Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
    }
}

@Composable
private fun AppearanceSection(isDark: Boolean, onThemeChange: (Boolean) -> Unit) {
    Column {
        Text("Appearance", fontWeight = FontWeight.Medium, fontSize = 16.sp)
        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FilterChip(
                selected = !isDark,
                onClick = { onThemeChange(false) },
                label = { Text("Light") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurface,
                    selectedContainerColor = Color(0xFF4CAF50),   // GREEN WHEN SELECTED
                    selectedLabelColor = Color.White
                )
            )

            FilterChip(
                selected = isDark,
                onClick = { onThemeChange(true) },
                label = { Text("Dark") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurface,
                    selectedContainerColor = Color(0xFF4CAF50),   // GREEN WHEN SELECTED
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun DataManagementSection( viewModel: TaskViewModel) {
    Column {
        Text("Data Management", fontWeight = FontWeight.Medium, fontSize = 16.sp)
        Spacer(Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
        ) {
            // Export Data
            ListItem(
                headlineContent = { Text("Export Data", fontWeight = FontWeight.SemiBold) },
                supportingContent = { Text("Download backup as JSON") },
                leadingContent = {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {

                    }
                    .padding(vertical = 8.dp)
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            // Clear All Data
            ListItem(
                headlineContent = { Text("Clear All Data", color = MaterialTheme.colorScheme.error) },
                supportingContent = { Text("Permanently delete all tasks") },
                leadingContent = {
                    Icon(Icons.Outlined.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF451010).copy(alpha = 0.4f))
                    .border(2.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .clickable {
                        viewModel.clearAll()
                    }
                    .padding(12.dp)
            )
        }
    }
}

