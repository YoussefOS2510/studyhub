package com.example.personalplanner.ui.theme

// ui/theme/Shape.kt

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Base Radius from CSS: 0.625rem (10dp)
private val BaseRadius = 10.dp

// Calculate derived radii
private val RadiusSm = BaseRadius - 4.dp // calc(var(--radius) - 4px) -> 6dp
private val RadiusMd = BaseRadius - 2.dp // calc(var(--radius) - 2px) -> 8dp
private val RadiusLg = BaseRadius       // var(--radius) -> 10dp
private val RadiusXl = BaseRadius + 4.dp // calc(var(--radius) + 4px) -> 14dp

val AppShapes = Shapes(
    // Small components (buttons, small containers)
    small = RoundedCornerShape(RadiusSm), // 6.dp
    // Medium components (cards, text fields)
    medium = RoundedCornerShape(RadiusMd), // 8.dp
    // Large components (large modals, dialogs)
    large = RoundedCornerShape(RadiusLg) // 10.dp
    // You can also define extraSmall and extraLarge if needed
)