package com.wikamay.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// PURPOSE: Text styles used across the app (MaterialTheme.typography.xxx).
// EDITABLE: Change font sizes / weights here to restyle all text at once.
private val AppTypography = Typography(
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 36.sp, lineHeight = 44.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 36.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 24.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp)
)

/**
 * PURPOSE: Wraps the whole app so every screen uses the same colors and text styles.
 * FUNCTION: Builds a Material 3 color scheme from the colors in Color.kt.
 */
@Composable
fun WiKamayTheme(content: @Composable () -> Unit) {
    val colors = lightColorScheme(
        primary = PrimarySlate,
        onPrimary = Color.White,
        primaryContainer = SoftBlueGray,
        onPrimaryContainer = PrimarySlate,
        background = ScreenBackground,
        onBackground = TextDark,
        surface = ScreenBackground,
        onSurface = TextDark,
        surfaceVariant = SoftBlueGray,
        onSurfaceVariant = TextMuted,
        outline = PrimarySlate
    )
    MaterialTheme(colorScheme = colors, typography = AppTypography, content = content)
}
