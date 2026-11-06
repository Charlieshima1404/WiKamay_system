package com.wikamay.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wikamay.app.ui.theme.PrimarySlate
import com.wikamay.app.ui.theme.TextDark
import com.wikamay.app.ui.theme.ToggleTrack

/** PURPOSE: The two translation directions of the app. */
enum class TranslationMode { SIGN_TO_TEXT, TEXT_TO_SIGN }

/**
 * PURPOSE: The pill-shaped translation direction switch at the top of both translation screens
 * (left half = Sign to Text, right half = Text to Sign, as in the wireframe).
 * FUNCTION: Highlights the [selected] side and calls [onModeSelected] when the user taps a side.
 */
@Composable
fun ModeToggle(
    selected: TranslationMode,
    onModeSelected: (TranslationMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(CircleShape)
            .background(ToggleTrack)
    ) {
        // EDITABLE: The labels shown inside the toggle.
        ToggleSegment("Sign → Text", selected == TranslationMode.SIGN_TO_TEXT) {
            onModeSelected(TranslationMode.SIGN_TO_TEXT)
        }
        ToggleSegment("Text → Sign", selected == TranslationMode.TEXT_TO_SIGN) {
            onModeSelected(TranslationMode.TEXT_TO_SIGN)
        }
    }
}

/** PURPOSE: One half of the toggle. Dark when selected, transparent otherwise. */
@Composable
private fun RowScope.ToggleSegment(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(CircleShape)
            .background(if (isSelected) PrimarySlate else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else TextDark,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}
