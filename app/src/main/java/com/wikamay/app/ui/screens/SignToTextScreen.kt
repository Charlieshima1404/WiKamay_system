package com.wikamay.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wikamay.app.data.SampleData
import com.wikamay.app.ui.components.CameraPlaceholder
import com.wikamay.app.ui.components.ModeToggle
import com.wikamay.app.ui.components.PlaceholderDialog
import com.wikamay.app.ui.components.RoundActionButton
import com.wikamay.app.ui.components.ScanFrame
import com.wikamay.app.ui.components.SquareIconButton
import com.wikamay.app.ui.components.TranslationMode
import com.wikamay.app.ui.theme.PanelGray
import com.wikamay.app.ui.theme.TextDark

// EDITABLE: Texts used on this screen.
private const val SCAN_HINT = "Place your hands inside the frame"
private const val EMPTY_RESULT_TEXT = "Tap the camera button to scan. The translation will appear here."

/**
 * PURPOSE: REVERSE TRANSLATION screen: Sign to Text/Speech.
 * Shows a fake camera preview with a scanning frame. Tapping the camera button shows a
 * sample translation (taken from SampleData.sampleTranslations).
 *
 * NAVIGATION:
 * - [onSwitchToTextToSign]: tapped on the "Text → Sign" side of the toggle.
 * - [onOpenLearningMenu]: tapped on the Learning Menu button (upper-right corner).
 */
@Composable
fun SignToTextScreen(
    onSwitchToTextToSign: () -> Unit,
    onOpenLearningMenu: () -> Unit
) {
    // PURPOSE: Which sample translation is displayed. -1 means "no scan yet".
    var resultIndex by rememberSaveable { mutableStateOf(-1) }

    // PURPOSE: Whether each placeholder pop-up is visible.
    var showScanDialog by remember { mutableStateOf(false) }
    var showSpeechDialog by remember { mutableStateOf(false) }

    // PLACEHOLDER: The whole background is a fake camera preview. Future: CameraX live preview.
    CameraPlaceholder(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // ---- Top row: Learning Menu button in the upper-right corner ----
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                SquareIconButton(
                    icon = Icons.Filled.School,
                    contentDescription = "Open Learning Menu",
                    onClick = onOpenLearningMenu // NAVIGATION: opens the Learning Menu.
                )
            }
            Spacer(Modifier.height(4.dp))

            // ---- Translation direction toggle ----
            ModeToggle(
                selected = TranslationMode.SIGN_TO_TEXT,
                onModeSelected = { mode ->
                    // NAVIGATION: Only the other side does something (we are already on Sign → Text).
                    if (mode == TranslationMode.TEXT_TO_SIGN) onSwitchToTextToSign()
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(280.dp)
            )

            // ---- Scanning frame (four corner brackets) ----
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                ScanFrame(modifier = Modifier.fillMaxSize())
                Text(
                    text = SCAN_HINT,
                    color = TextDark,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // ---- Camera / scan button ----
            // PLACEHOLDER: Future development starts hand tracking + FSL recognition here.
            RoundActionButton(
                icon = Icons.Filled.CameraAlt,
                contentDescription = "Scan sign (camera)",
                onClick = {
                    // FUNCTION: Show the next sample translation and explain it is a placeholder.
                    resultIndex = (resultIndex + 1) % SampleData.sampleTranslations.size
                    showScanDialog = true
                },
                size = 64.dp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(12.dp))

            // ---- Translated text panel with the text-to-speech button ----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(PanelGray)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // PURPOSE: Shows the (sample) translated FSL text.
                Text(
                    text = if (resultIndex < 0) EMPTY_RESULT_TEXT
                    else SampleData.sampleTranslations[resultIndex],
                    color = TextDark,
                    style = if (resultIndex < 0) MaterialTheme.typography.bodyMedium
                    else MaterialTheme.typography.titleLarge,
                    fontWeight = if (resultIndex < 0) FontWeight.Normal else FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                // PLACEHOLDER: Future development reads the text aloud (Android TextToSpeech).
                RoundActionButton(
                    icon = Icons.Filled.VolumeUp,
                    contentDescription = "Read translation aloud",
                    onClick = { showSpeechDialog = true },
                    size = 48.dp
                )
            }
        }
    }

    if (showScanDialog) {
        PlaceholderDialog(
            title = "Camera scan (placeholder)",
            message = "Camera-based FSL recognition will be implemented in a later stage of development. The text shown is only a sample translation.",
            onDismiss = { showScanDialog = false }
        )
    }
    if (showSpeechDialog) {
        PlaceholderDialog(
            title = "Text-to-speech (placeholder)",
            message = "Reading the translation aloud will be implemented in a later stage of development.",
            onDismiss = { showSpeechDialog = false }
        )
    }
}
