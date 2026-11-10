
package com.wikamay.app.ui.screens

import androidx.camera.core.CameraSelector
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
import androidx.compose.material3.TextButton
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

import com.wikamay.app.ui.components.CameraXPreview
import com.wikamay.app.ui.components.ModeToggle
import com.wikamay.app.ui.components.PlaceholderDialog
import com.wikamay.app.ui.components.RoundActionButton
import com.wikamay.app.ui.components.ScanFrame
import com.wikamay.app.ui.components.SquareIconButton
import com.wikamay.app.ui.components.TranslationMode
import com.wikamay.app.ui.theme.PanelGray
import com.wikamay.app.ui.theme.TextDark

private const val SCAN_HINT =
    "Place your hands inside the frame"

private const val EMPTY_RESULT_TEXT =
    "Your sign translation will appear here."

@Composable
fun SignToTextScreen(
    onSwitchToTextToSign: () -> Unit,
    onOpenLearningMenu: () -> Unit
) {

    // Camera selection
    var isFrontCamera by rememberSaveable {
        mutableStateOf(true)
    }

    // Dialog visibility
    var showScanDialog by remember {
        mutableStateOf(false)
    }

    var showSpeechDialog by remember {
        mutableStateOf(false)
    }

    // CameraX live preview
    CameraXPreview(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(0.dp),
        lensFacing = if (isFrontCamera) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(
                    horizontal = 20.dp,
                    vertical = 12.dp
                )
        ) {

            // TOP ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {

                TextButton(
                    onClick = {
                        isFrontCamera = !isFrontCamera
                    }
                ) {
                    Text(
                        text = if (isFrontCamera) {
                            "Switch to Back"
                        } else {
                            "Switch to Front"
                        },
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                SquareIconButton(
                    icon = Icons.Filled.School,
                    contentDescription = "Open Learning Menu",
                    onClick = onOpenLearningMenu
                )
            }

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            // TRANSLATION MODE
            ModeToggle(
                selected = TranslationMode.SIGN_TO_TEXT,
                onModeSelected = { mode ->
                    if (mode == TranslationMode.TEXT_TO_SIGN) {
                        onSwitchToTextToSign()
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(280.dp)
            )

            // LIVE CAMERA SCANNING AREA
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(
                        horizontal = 24.dp,
                        vertical = 24.dp
                    )
            ) {

                // Scanning brackets
                ScanFrame(
                    modifier = Modifier.fillMaxSize()
                )

                // Camera instruction
                Text(
                    text = SCAN_HINT,
                    color = androidx.compose.ui.graphics.Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(
                            androidx.compose.ui.graphics.Color.Black.copy(
                                alpha = 0.55f
                            ),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(10.dp)
                )
            }

            // CAMERA SCAN BUTTON
            RoundActionButton(
                icon = Icons.Filled.CameraAlt,
                contentDescription = "Scan sign using camera",
                onClick = {
                    showScanDialog = true
                },
                size = 64.dp,
                modifier = Modifier.align(
                    Alignment.CenterHorizontally
                )
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // TRANSLATION RESULT PANEL
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 80.dp)
                    .clip(
                        RoundedCornerShape(16.dp)
                    )
                    .background(PanelGray)
                    .padding(16.dp),

                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = EMPTY_RESULT_TEXT,
                    color = TextDark,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                )

                // TEXT TO SPEECH
                RoundActionButton(
                    icon = Icons.Filled.VolumeUp,
                    contentDescription = "Read translation aloud",
                    onClick = {
                        showSpeechDialog = true
                    },
                    size = 48.dp
                )
            }
        }
    }

    // CAMERA SCAN INFORMATION
    if (showScanDialog) {

        PlaceholderDialog(
            title = "FSL Recognition",
            message = "Your live camera preview is available. Automatic Filipino Sign Language recognition still needs to be connected to a hand detection and recognition model.",
            onDismiss = {
                showScanDialog = false
            }
        )
    }

    // TEXT TO SPEECH INFORMATION
    if (showSpeechDialog) {

        PlaceholderDialog(
            title = "Text-to-speech",
            message = "Text-to-speech will be connected after the sign recognition system is implemented.",
            onDismiss = {
                showSpeechDialog = false
            }
        )
    }
}
