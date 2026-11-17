
package com.wikamay.app.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import com.wikamay.app.data.SampleData
import com.wikamay.app.ui.components.ArCameraPanel
import com.wikamay.app.ui.components.ArReferencePreview
import com.wikamay.app.ui.components.LearningTopBar
import com.wikamay.app.ui.components.PlaceholderBox
import com.wikamay.app.ui.components.PlaceholderDialog
import com.wikamay.app.ui.components.PrimaryPillButton
import com.wikamay.app.ui.components.RoundActionButton
import com.wikamay.app.ui.components.SecondaryPillButton
import com.wikamay.app.ui.theme.PrimarySlate
import com.wikamay.app.ui.theme.TextMuted

// ==========================================
// SCREEN TRANSITION ANIMATION
// ==========================================

private const val AR_TRANSITION_DURATION_MS = 450

// ==========================================
// AR INTERFACE DESIGN
// ==========================================

private val AR_TITLE_COLOR = Color.White
private const val AR_BOTTOM_FADE = 0.6f
private val VIDEO_LABEL_BOTTOM_PADDING = 72.dp

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

    var lessonPosition by rememberSaveable {
        mutableStateOf(
            SampleData.lessons.indexOfFirst {
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

    // Switch between front and back cameras
    val switchCamera: () -> Unit = {
        isFrontCamera = !isFrontCamera
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

    val lesson = SampleData.lessons[lessonPosition]

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
        // AR CAMERA PANEL
        // ==========================================
        // CameraX live preview with front/back switching.
        // Camera switch button is positioned at the center
        // inside ArCameraPanel.

        if (arProgress > 0f) {

            ArCameraPanel(
                progress = arProgress,
                startBounds = videoBoundsInRoot.translate(-containerOrigin),

                // Current camera selection
                isFrontCamera = isFrontCamera,

                // Switch front/back camera
                onSwitchCamera = switchCamera
            )
        }

        // ==========================================
        // ORIGINAL LESSON PAGE
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

            // Navigation
            LearningTopBar(
                onBack = if (isArMode) closeAr else onBack,
                onReturnToTranslation = onReturnToTranslation
            )

            Spacer(Modifier.height(12.dp))

            // Lesson title
            Text(
                "Lesson ${lesson.id}",
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

            // ==========================================
            // VIDEO PLACEHOLDER
            // ==========================================

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

                PlaceholderBox(
                    modifier = Modifier.fillMaxSize(),
                    icon = Icons.Filled.Videocam
                )

                // Play button
                RoundActionButton(
                    icon = Icons.Filled.PlayArrow,
                    contentDescription = "Play lesson video",
                    onClick = {
                        showVideoDialog = true
                    },
                    size = 64.dp,
                    enabled = !isArMode,
                    modifier = Modifier.align(Alignment.Center)
                )

                // Video label
                Text(
                    text = "Sign video (placeholder)",
                    color = TextMuted,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(
                            bottom = VIDEO_LABEL_BOTTOM_PADDING
                        )
                )

                // ==========================================
                // TRY BUTTON
                // ==========================================

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

            // ==========================================
            // SIGN NAME AND DESCRIPTION
            // ==========================================

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

                Text(
                    lesson.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            }

            Spacer(Modifier.height(12.dp))

            // ==========================================
            // PREVIOUS / NEXT LESSON
            // ==========================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(arBottomAlpha),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // Previous lesson
                SecondaryPillButton(
                    text = "Previous lesson",
                    onClick = {
                        if (lessonPosition > 0) {
                            lessonPosition--
                        }
                    },
                    enabled = lessonPosition > 0 && !isArMode
                )

                // Next lesson
                PrimaryPillButton(
                    text = "Next lesson",
                    onClick = {
                        if (lessonPosition < SampleData.lessons.lastIndex) {
                            lessonPosition++
                        }
                    },
                    enabled = lessonPosition < SampleData.lessons.lastIndex &&
                            !isArMode
                )
            }

            Spacer(Modifier.height(8.dp))
        }

        // ==========================================
        // AR REFERENCE PREVIEW
        // ==========================================

        ArReferencePreview(
            progress = arProgress
        )
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
