// Add this import for Surface
import androidx.compose.material3.Surface
// Add this import for the Google Icon
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GoogleLoginScreen(
    onGoogleLoginClick: () -> Unit,
    modifier: Modifier
) {
    // The main screen column for the title/icon and the card
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // App Icon (The purple '< />')
        //         // This composable should represent your icon, e.g., a custom Image or Icon
        // For simplicity, I'll use a placeholder structure:
        // Text(
        //    text = "< />",
        //    fontSize = 50.sp,
        //    color = Color(0xFF9C27B0) // Purple color from the image
        // )

        Spacer(modifier = Modifier.height(16.dp))

        // App Title
        Text(

            text = "study-planner",
            style = MaterialTheme.typography.headlineLarge,
            fontFamily = FontFamily.SansSerif // Matching the sans-serif font
        )

        // App Tagline
        Text(
            text = "Track your study progress like a developer",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(64.dp))

        // The main content card (dark box in the image)
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f) // Slightly less than full width
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(12.dp),
            // Use 'surface' color from the theme for the card background
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Welcome back",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(24.dp))

                // The Custom Google Sign-In Button
                GoogleSignInButton(
                    onClick = onGoogleLoginClick
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Quick & Secure Text
                Text(
                    text = "Quick & Secure",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Disclaimer Text
                Text(
                    text = "By signing in, you agree to store your study data locally on this device",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    // Aligning the text to the center (though it's full width here)
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                )
            }
        }
    }
}
@Composable
fun GoogleSignInButton(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        // Override theme colors for the Google Sign-In button style
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White, // Always white background
            contentColor = Color.Black // Always black text
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            // Optional: Add a light gray border for contrast on light backgrounds
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
            /*   Image(
        painter = painterResource(id = ), // Replace with your actual drawable
        contentDescription = "Google Logo",
        modifier = Modifier.height(24.dp).padding(end = 8.dp)
        )*/
        Text(
            text = "Sign in with Google",
            fontSize = 16.sp
        )
    }
}