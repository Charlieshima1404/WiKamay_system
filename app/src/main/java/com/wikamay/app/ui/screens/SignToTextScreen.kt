
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
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext

import com.wikamay.app.ui.components.CameraXPreview
import com.wikamay.app.ui.components.ModeToggle
import com.wikamay.app.ui.components.PlaceholderDialog
import com.wikamay.app.ui.components.RoundActionButton
import com.wikamay.app.ui.components.ScanFrame
import com.wikamay.app.ui.components.SquareIconButton
import com.wikamay.app.ui.components.TranslationMode
import com.wikamay.app.ui.components.HandOverlay
import com.wikamay.app.ui.theme.PanelGray
import com.wikamay.app.ui.theme.TextDark

import com.wikamay.app.ml.HandLandmark
import com.wikamay.app.ml.HandLandmarkPreprocessor
import com.wikamay.app.ml.Sequence
import com.wikamay.app.ml.EnglishSentenceBuilder

private const val SCAN_HINT =
    "Place your hands inside the frame"

private const val EMPTY_RESULT_TEXT =
    "Your sign translation will appear here."

private const val HAND_SCALE = 1.2f

@Composable
fun SignToTextScreen(
    onSwitchToTextToSign: () -> Unit,
    onOpenLearningMenu: () -> Unit
) {

    // PURPOSE: MediaPipe detector
    val context = LocalContext.current

    val detectionMessage = remember {
        mutableStateOf(SCAN_HINT)
    }

    val recognizedText = remember {
        mutableStateOf(EMPTY_RESULT_TEXT)
    }

    var isRecognizing by rememberSaveable {
        mutableStateOf(false)
    }

    val sentenceBuilder = remember {
        EnglishSentenceBuilder()
    }

    val handLandmarkPoints = remember {
        mutableStateOf<List<List<Offset>>>(emptyList())
    }

    val recognitionManager = remember(context) {
        Sequence(
            context = context,

            onUpdate = { update ->

                val prediction = update.selectedPrediction

                if (prediction != null) {
                    sentenceBuilder.accept(prediction)?.let { sentence ->
                        recognizedText.value = sentence
                    }
                }
            },

            onError = { error ->
                detectionMessage.value =
                    "Recognition error: $error"
            }
        )
    }

    DisposableEffect(recognitionManager) {
        onDispose {
            recognitionManager.close()
        }
    }

    val handLandmarker = remember(context) {
        HandLandmark(
            context = context,

            onResult = { result ->
                val extracted =
                    HandLandmarkPreprocessor.extract(result)

                handLandmarkPoints.value = result.landmarks().map { hand ->
                    hand.map { landmark ->
                        Offset(
                            x = landmark.x(),
                            y = landmark.y()
                        )
                    }
                }

                recognitionManager.processFrame(extracted)
            },

            onError = { error ->
                detectionMessage.value =
                    "Hand detection error: $error"
            }
        )
    }

    DisposableEffect(handLandmarker) {
        onDispose {
            handLandmarker.close()
        }
    }

    // Camera selection
    var isFrontCamera by rememberSaveable {
        mutableStateOf(true)
    }

    var cameraRotationDegrees by remember {
        mutableStateOf(270)
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
        },
        onFrame = { imageProxy ->
            try {
                cameraRotationDegrees = imageProxy.imageInfo.rotationDegrees

                handLandmarker.detect(imageProxy)
            } catch (exception: Exception) {
                detectionMessage.value =
                    "Frame processing error: ${exception.localizedMessage}"
            }
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            HandOverlay(
                handLandmarkPoints = handLandmarkPoints.value,
                cameraRotationDegrees = cameraRotationDegrees,
                isFrontCamera = isFrontCamera,
                modifier = Modifier.fillMaxSize()
            )

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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // CAMERA SWITCH ICON BUTTON
                    SquareIconButton(
                        icon = Icons.Filled.Cameraswitch,
                        contentDescription = "Switch camera",
                        onClick = {
                            isFrontCamera = !isFrontCamera
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // LEARNING MENU BUTTON
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
                    // SCANNING BRACKETS
                    ScanFrame(
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // CAMERA SCAN BUTTON
                RoundActionButton(
                    icon = if (isRecognizing) {
                        Icons.Filled.Stop
                    } else {
                        Icons.Filled.CameraAlt
                    },
                    contentDescription = if (isRecognizing) {
                        "End recognition"
                    } else {
                        "Start recognition"
                    },
                    onClick = {
                        if (isRecognizing) {
                            recognitionManager.stopSession()
                            isRecognizing = false
                        } else {
                            sentenceBuilder.reset()
                            recognizedText.value = EMPTY_RESULT_TEXT

                            recognitionManager.startSession()
                            isRecognizing = true
                        }
                    },
                    size = 64.dp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
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
                        text = recognizedText.value,
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
}


