package com.wikamay.app.ui.screens //ito yung inside ng FSL WORDS YUNG AY CAMERA//


import android.util.Log


import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.translate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp


import com.wikamay.app.data.SampleData
import com.wikamay.app.ml.HandLandmark
import com.wikamay.app.ml.HandLandmarkPreprocessor
import com.wikamay.app.ml.Sequence
import com.wikamay.app.ui.components.ArCameraPanel
import com.wikamay.app.ui.components.ArReferencePreview
import com.wikamay.app.ui.components.LearningMedia
import com.wikamay.app.ui.components.LearningTopBar
import com.wikamay.app.ui.components.PlaceholderDialog
import com.wikamay.app.ui.components.PrimaryPillButton
import com.wikamay.app.ui.components.SecondaryPillButton
import com.wikamay.app.ui.theme.PrimarySlate
import com.wikamay.app.ui.theme.TextMuted
import androidx.compose.ui.zIndex


// ==========================================
// SCREEN TRANSITION ANIMATION
// ==========================================


private const val AR_TRANSITION_DURATION_MS = 450


// ==========================================
// AR INTERFACE DESIGN
// ==========================================


private val AR_TITLE_COLOR = Color.White
private const val AR_BOTTOM_FADE = 0.6f


/**
 * PURPOSE:
 * Lesson details screen.
 *
 * FUNCTION:
 * Shows one lesson. Previous/Next buttons switch between lessons.
 *
 * NAVIGATION:
 * onBack returns to Tutorials.
 * onReturnToTranslation opens Translation.
 *
 * AR MODE:
 * Back button and phone Back gesture close AR mode first.
 *
 * SIGN RECOGNITION:
 * Uses MediaPipe hand landmarks and Sequence recognition
 * to determine whether the user performs the expected sign.
 */


@Composable
fun LessonDetailScreen(
    initialLessonId: Int,
    onBack: () -> Unit,
    onReturnToTranslation: () -> Unit
) {


    // ==========================================
    // LESSON STATE
    // ==========================================


    // Lessons belonging to the same category
    // as the initially opened lesson.


    val categoryLessons = remember(initialLessonId) {
        val opened = SampleData.lessons.firstOrNull {
            it.id == initialLessonId
        }


        if (opened == null) {
            SampleData.lessons
        } else {
            SampleData.lessons.filter {
                it.category == opened.category
            }
        }
    }


    // Initial lesson position


    var lessonPosition by rememberSaveable(initialLessonId) {
        mutableStateOf(
            categoryLessons.indexOfFirst {
                it.id == initialLessonId
            }.coerceAtLeast(0)
        )
    }


    // Video placeholder dialog


    var showVideoDialog by remember {
        mutableStateOf(false)
    }


    // ==========================================
    // AR MODE STATE
    // ==========================================


    var isArMode by rememberSaveable {
        mutableStateOf(false)
    }


    // ==========================================
    // CAMERA SWITCH STATE
    // ==========================================


    // false = Back camera
    // true = Front camera


    var isFrontCamera by rememberSaveable {
        mutableStateOf(false)
    }


    val switchCamera: () -> Unit = {
        isFrontCamera = !isFrontCamera
    }


    var cameraRotationDegrees by remember {
        mutableStateOf(270)
    }


    val handLandmarkPoints = remember {
        mutableStateOf<List<List<Offset>>>(emptyList())
    }


    // ==========================================
    // SCREEN TRANSITION ANIMATION
    // ==========================================


    val arProgress by animateFloatAsState(
        targetValue = if (isArMode) 1f else 0f,
        animationSpec = tween(
            durationMillis = AR_TRANSITION_DURATION_MS,
            easing = FastOutSlowInEasing
        ),
        label = "arTransition"
    )


    // ==========================================
    // TRY BUTTON ACTION
    // ==========================================


    val openAr: () -> Unit = {
        isArMode = true
    }


    val closeAr: () -> Unit = {
        isArMode = false
    }


    // Phone Back gesture closes AR first


    BackHandler(enabled = isArMode) {
        closeAr()
    }


    // ==========================================
    // SCREEN POSITION TRACKING
    // ==========================================


    var videoBoundsInRoot by remember {
        mutableStateOf(Rect.Zero)
    }


    var containerOrigin by remember {
        mutableStateOf(Offset.Zero)
    }


    // ==========================================
    // AR TEXT ANIMATION
    // ==========================================


    val arBottomAlpha = 1f - AR_BOTTOM_FADE * arProgress


    val arTitleProgress =
        ((arProgress - 0.55f) / 0.45f).coerceIn(0f, 1f)


    // Current lesson


    val lesson = categoryLessons[lessonPosition]


    // ==========================================
    // SIGN RECOGNITION STATE
    // ==========================================


    val context = LocalContext.current


    // Expected sign updates whenever the lesson changes


    val expectedLabel = rememberUpdatedState(lesson.signName)


    // Recognition feedback


    var isCorrect by remember {
        mutableStateOf(false)
    }


    var matchingPredictionCount by remember {
        mutableStateOf(0)
    }


    var lastMatchingLabel by remember {
        mutableStateOf("")
    }


    // ==========================================
    // RECOGNITION MANAGER
    // ==========================================


    val recognitionManager = remember(context) {
        Sequence(
            context = context,


            onUpdate = { update ->


                // Dynamic prediction for sign practice


                val prediction = update.dynamicPrediction


                if (prediction != null) {


                    val predictedLabel = prediction.label
                        .trim()
                        .uppercase()
                        .replace("_", " ")
                        .replace(Regex("\\s+"), " ")


                    val targetLabel = expectedLabel.value
                        .trim()
                        .uppercase()
                        .replace("_", " ")
                        .replace(Regex("\\s+"), " ")


                    if (predictedLabel == targetLabel) {


                        // Count consecutive matching predictions


                        if (lastMatchingLabel == predictedLabel) {


                            matchingPredictionCount++


                        } else {


                            lastMatchingLabel = predictedLabel
                            matchingPredictionCount = 1
                        }


                        // Require 3 consecutive matching predictions


                        if (matchingPredictionCount >= 3) {
                            isCorrect = true
                        }


                    } else {


                        // Reset when the prediction does not match


                        matchingPredictionCount = 0
                        lastMatchingLabel = ""
                        isCorrect = false
                    }
                }
            },


            onError = { message ->
                Log.e("LessonRecognition", message)
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


                // Extract hand landmark features


                val extracted =
                    HandLandmarkPreprocessor.extract(result)


                // Convert landmarks to Offset points for AR overlay


                handLandmarkPoints.value = result.landmarks().map { hand ->
                    hand.map { landmark ->
                        Offset(
                            x = landmark.x(),
                            y = landmark.y()
                        )
                    }
                }


                // Send extracted features to recognition manager


                recognitionManager.processFrame(extracted)
            },


            onError = { message ->
                Log.e("LessonHandLandmark", message)
            }
        )
    }


    // ==========================================
    // RECOGNITION SESSION
    // ==========================================


    LaunchedEffect(isArMode) {


        if (isArMode) {


            // Reset recognition state when AR starts


            matchingPredictionCount = 0
            lastMatchingLabel = ""
            isCorrect = false


            recognitionManager.startSession()


        } else {


            // Stop recognition when AR closes


            recognitionManager.stopSession()


            matchingPredictionCount = 0
            lastMatchingLabel = ""
            isCorrect = false
        }
    }


    // ==========================================
    // RESOURCE CLEANUP
    // ==========================================


    DisposableEffect(handLandmarker, recognitionManager) {


        onDispose {


            recognitionManager.stopSession()


            handLandmarker.close()


            recognitionManager.close()
        }
    }


    // ==========================================
    // ROOT CONTAINER
    // ==========================================



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .onGloballyPositioned {
                containerOrigin = it.positionInRoot()
            }
    ) {

        // ==========================================
        // 1. ORIGINAL LESSON PAGE
        // ==========================================

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(
                    horizontal = 20.dp,
                    vertical = 12.dp
                )
        ) {

            Spacer(Modifier.height(12.dp))

            Text(
                "Lesson ${lessonPosition + 1}",
                style = MaterialTheme.typography.headlineMedium,
                color = lerp(
                    PrimarySlate,
                    AR_TITLE_COLOR,
                    arTitleProgress
                )
            )

            Text(
                lesson.category,
                style = MaterialTheme.typography.titleMedium,
                color = lerp(
                    TextMuted,
                    AR_TITLE_COLOR,
                    arTitleProgress
                )
            )

            Spacer(Modifier.height(12.dp))

            // LESSON VIDEO / IMAGE

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .onGloballyPositioned {
                        videoBoundsInRoot = it.boundsInRoot()
                    }
                    .graphicsLayer {
                        alpha = 1f - arProgress
                    }
            ) {

                LearningMedia(
                    category = lesson.category,
                    item = lesson.signName,
                    modifier = Modifier.fillMaxSize(),
                    label = null
                )

                PrimaryPillButton(
                    text = "TRY",
                    onClick = openAr,
                    enabled = !isArMode,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            // SIGN NAME AND DESCRIPTION

            Column(
                modifier = Modifier
                    .alpha(arBottomAlpha)
                    .verticalScroll(rememberScrollState())
            ) {

                Text(
                    lesson.signName,
                    style = MaterialTheme.typography.titleLarge,
                    color = PrimarySlate
                )

                Spacer(Modifier.height(4.dp))
            }

            Spacer(Modifier.height(12.dp))

            // PREVIOUS / NEXT LESSON

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(arBottomAlpha),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                SecondaryPillButton(
                    text = "Previous lesson",
                    onClick = {
                        if (lessonPosition > 0) {
                            lessonPosition--
                        }
                    },
                    enabled = lessonPosition > 0 && !isArMode
                )

                PrimaryPillButton(
                    text = "Next lesson",
                    onClick = {
                        if (lessonPosition < categoryLessons.lastIndex) {
                            lessonPosition++
                        }
                    },
                    enabled = lessonPosition < categoryLessons.lastIndex &&
                            !isArMode
                )
            }

            Spacer(Modifier.height(8.dp))
        }


        // ==========================================
// 2. AR CAMERA PANEL
// ==========================================

        if (arProgress > 0f) {
            ArCameraPanel(
                progress = arProgress,
                startBounds = videoBoundsInRoot.translate(-containerOrigin),
                isFrontCamera = isFrontCamera,
                onSwitchCamera = switchCamera,
                onFrame = { imageProxy ->
                    cameraRotationDegrees =
                        imageProxy.imageInfo.rotationDegrees

                    handLandmarker.detect(imageProxy)
                },
                isCorrect = isCorrect,
                handLandmarkPoints = handLandmarkPoints.value,
                cameraRotationDegrees = cameraRotationDegrees
            )
        }

// ==========================================
// 3. AR REFERENCE PREVIEW
// ==========================================

        ArReferencePreview(
            progress = arProgress,
            category = lesson.category,
            item = lesson.signName,
            modifier = Modifier.zIndex(2f)
        )

// ==========================================
// 4. TOP NAVIGATION
// ==========================================

        Box(
            modifier = Modifier.zIndex(3f)
        ) {
            LearningTopBar(
                onBack = if (isArMode) closeAr else onBack,
                onReturnToTranslation = onReturnToTranslation
            )
        }
    }



    // ==========================================
    // VIDEO PLACEHOLDER DIALOG
    // ==========================================


    if (showVideoDialog) {


        PlaceholderDialog(
            title = "Video (placeholder)",


            message = "Tutorial videos will be added in a later stage of development.",


            onDismiss = {
                showVideoDialog = false
            }
        )
    }
}








