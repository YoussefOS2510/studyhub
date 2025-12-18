// ui/theme/Type.kt


import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

// 2. Map styles to the Material 3 Typography system using FontFamily.Monospace
val AppTypography = Typography(
    // --------------------------------------------------------------------------------
    // Ensure all styles that the Text composable might default to are set to Monospace.
    // bodyLarge is the primary default used by unstyled Text composables.
    // --------------------------------------------------------------------------------

    // Default for most unstyled text (like in your Greeting function)
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Monospace, // <-- CRITICAL: Setting default body text
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 1.5.em
    ),

    // CSS p, label, input (var(--text-base), 400/500) -> bodyMedium
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal, // 400 for P/Input
        fontSize = 16.sp, // Equivalent to --text-base
        lineHeight = 1.5.em
    ),

    // CSS h1 (var(--text-2xl), 500) -> displaySmall (or equivalent)
    displaySmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium, // 500
        fontSize = 24.sp, // Equivalent to --text-2xl/h1
        lineHeight = 1.5.em
    ),

    // CSS h2 (var(--text-xl), 500) -> headlineSmall
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium, // 500
        fontSize = 20.sp, // Equivalent to --text-xl/h2
        lineHeight = 1.5.em
    ),

    // Example for buttons/labels which often use 'Label Medium' or 'Title Small'
    labelMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium, // 500
        fontSize = 12.sp,
        lineHeight = 1.5.em
    ),

    // Example for app bar titles
    titleLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 1.5.em
    )
)