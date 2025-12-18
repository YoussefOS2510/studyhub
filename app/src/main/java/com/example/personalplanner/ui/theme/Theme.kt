import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.example.personalplanner.ui.theme.AppShapes

// In Theme.kt

private val LightColorPalette = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightPrimaryForeground,
    secondary = LightSecondary,
    onSecondary = LightSecondaryForeground,
    background = LightBackground,
    surface = LightCard, // Often Card/Popover in CSS context
    onSurface = LightCardForeground,
    error = LightDestructive,
    onError = LightDestructiveForeground,
    // You can map Muted/Accent/Border etc., to extended properties or custom objects
)

private val DarkColorPalette = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkPrimaryForeground,
    secondary = DarkSecondary,
    onSecondary = DarkSecondaryForeground,
    background = DarkBackground,
    surface = DarkCard,
    onSurface = DarkCardForeground,
    error = DarkDestructive,
    onError = DarkDestructiveForeground,
)

@Composable
fun StudyplannerappTheme(
    darkTheme: Boolean ,
    // ... other parameters
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}