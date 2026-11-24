package com.wikamay.app.ui.screens //INSIDE THE ALPHABET/NUMBERS YUNG MAY AR//

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wikamay.app.ui.components.CameraXPreview
import androidx.camera.core.CameraSelector
import com.wikamay.app.ui.components.LearningTopBar
import com.wikamay.app.ui.components.RoundActionButton
import com.wikamay.app.ui.components.ScanFrame
import com.wikamay.app.ui.theme.PrimarySlate
import com.wikamay.app.ui.theme.TextMuted
import android.util.Log
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import com.wikamay.app.ml.HandLandmark
import com.wikamay.app.ml.HandLandmarkPreprocessor
import com.wikamay.app.ml.Sequence
import com.wikamay.app.ui.components.HandOverlay
import androidx.compose.ui.geometry.Offset
import com.wikamay.app.ui.components.LearningMedia

/**
 * PURPOSE: Practice screen shared by the FSL Alphabet and Numbers sections
 * (wireframe: "Align your hand to the overlay" with a camera area, corner brackets and a
 * reference box at the top).
 *
 * FUNCTION: Shows the selected letter/number, a reference handshape placeholder, a practice area
 * (fake camera), and Previous/Next buttons that change the displayed item.
 *
 * NAVIGATION: [onBack] returns to the selection grid; [onReturnToTranslation] jumps to Translation.
 *
 * @param itemKind "Letter" or "Number" (only used for labels).
 * @param startIndex Position of the item the user tapped on the selection grid.
 */
@Composable
fun PracticeScreen(
    screenTitle: String,
    itemKind: String,
    items: List<String>,
    startIndex: Int,
    onBack: () -> Unit,
    onReturnToTranslation: () -> Unit
) {
    var currentIndex by rememberSaveable {
        mutableStateOf(startIndex.coerceIn(0, items.lastIndex))
    }

    val currentItem = items[currentIndex]

// Camera selection: false = back, true = front.
    var isFrontCamera by rememberSaveable {
        mutableStateOf(false)
    }

// Recognition feedback.
    var isCorrect by remember {
        mutableStateOf(false)
    }

    var matchingPredictionCount by remember {
        mutableStateOf(0)
    }

    var lastMatchingLabel by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current
    val expectedItem = rememberUpdatedState(currentItem)

    var cameraRotationDegrees by remember {
        mutableStateOf(270)
    }

    val handLandmarkPoints = remember {
        mutableStateOf<List<List<Offset>>>(emptyList())
    }

    // ==========================================
    // RECOGNITION MANAGER
    // ==========================================

    val recognitionManager = remember(context) {
        Sequence(
            context = context,
            onUpdate = { update ->

                val prediction = update.selectedPrediction

                if (prediction != null) {

                    val predictedLabel = prediction.label
                        .trim()
                        .uppercase()
                        .replace("_", " ")
                        .replace(Regex("\\s+"), " ")

                    val targetLabel = expectedItem.value
                        .trim()
                        .uppercase()
                        .replace("_", " ")
                        .replace(Regex("\\s+"), " ")

                    if (predictedLabel == targetLabel) {

                        if (lastMatchingLabel == predictedLabel) {
                            matchingPredictionCount++
                        } else {
                            lastMatchingLabel = predictedLabel
                            matchingPredictionCount = 1
                        }

                        if (matchingPredictionCount >= 3) {
                            isCorrect = true
                        }

                    } else {
                        matchingPredictionCount = 0
                        lastMatchingLabel = ""
                        isCorrect = false
                    }
                }
            },
            onError = { message ->
                Log.e("PracticeRecognition", message)
            }
        )
    }

    // ==========================================
    // HAND LANDMARK DETECTOR
    // ==========================================

    val handLandmarker = remember(context, recognitionManager) {
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
            onError = { message ->
                Log.e("PracticeHandLandmark", message)
            }
        )
    }

    // ==========================================
    // RECOGNITION SESSION
    // ==========================================

    LaunchedEffect(currentItem) {

        matchingPredictionCount = 0
        lastMatchingLabel = ""
        isCorrect = false

        recognitionManager.startSession()
    }

    DisposableEffect(handLandmarker, recognitionManager) {
        onDispose {
            recognitionManager.stopSession()
            handLandmarker.close()
            recognitionManager.close()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        LearningTopBar(onBack = onBack, onReturnToTranslation = onReturnToTranslation)
        Spacer(Modifier.height(8.dp))

        Text(
            text = "$screenTitle: $itemKind $currentItem",
            style = MaterialTheme.typography.titleLarge,
            color = PrimarySlate,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))


// ---- Practice area: Live CameraX + scan frame + reference handshape ----
        CameraXPreview(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            lensFacing = if (isFrontCamera) {
                CameraSelector.LENS_FACING_FRONT
            } else {
                CameraSelector.LENS_FACING_BACK
            },
            onFrame = { imageProxy ->
                try {
                    cameraRotationDegrees =
                        imageProxy.imageInfo.rotationDegrees

                    handLandmarker.detect(imageProxy)
                } catch (exception: Exception) {
                    Log.e(
                        "PracticeCamera",
                        "Frame processing failed",
                        exception
                    )
                }
            }
        ) {
            HandOverlay(
                handLandmarkPoints = handLandmarkPoints.value,
                cameraRotationDegrees = cameraRotationDegrees,
                isFrontCamera = isFrontCamera,
                modifier = Modifier.fillMaxSize()
            )

            // Scan frame overlay
            ScanFrame(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                isCorrect = isCorrect
            )

            // Reference handshape media
            LearningMedia(
                category = if (itemKind.equals("Letter", ignoreCase = true)) {
                    "Alphabet"
                } else {
                    "Numbers"
                },
                item = currentItem,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 28.dp)
                    .size(width = 150.dp, height = 90.dp)
            )

            // Camera switch button
            RoundActionButton(
                icon = Icons.Filled.Cameraswitch,
                contentDescription = "Switch camera",
                onClick = {
                    isFrontCamera = !isFrontCamera
                },
                size = 48.dp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            )
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text = "Align your hand to the overlay",
            style = MaterialTheme.typography.bodyLarge,
            color = TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        // ---- Previous / current item / Next ----
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // FUNCTION: Goes to the previous item. Disabled on the first item.
            RoundActionButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Previous $itemKind",
                onClick = { if (currentIndex > 0) currentIndex-- },
                enabled = currentIndex > 0
            )
            // PURPOSE: Large display of the selected letter/number.
            Text(
                text = currentItem,
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = PrimarySlate
            )
            // FUNCTION: Goes to the next item. Disabled on the last item.
            RoundActionButton(
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next $itemKind",
                onClick = { if (currentIndex < items.lastIndex) currentIndex++ },
                enabled = currentIndex < items.lastIndex
            )
        }
        Text(
            text = "${currentIndex + 1} of ${items.size}",
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

    }
}
