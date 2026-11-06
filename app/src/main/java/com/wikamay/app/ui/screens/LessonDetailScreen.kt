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
// EDIT HERE: Adjust duration and feel of the lesson page -> AR transition.
// ==========================================

// EDITABLE: How long (milliseconds) the page takes to move into AR mode and back. Larger = slower.
private const val AR_TRANSITION_DURATION_MS = 450

// ==========================================
// AR INTERFACE DESIGN
// EDIT HERE: Appearance of the lesson page while AR mode is open.
// ==========================================

// EDITABLE: Color the lesson title/category turn into while AR mode is open (wireframe: white on gray).
private val AR_TITLE_COLOR = Color.White

// EDITABLE: How faded the description and Previous/Next buttons become in AR mode (0f = unchanged, 1f = invisible).
private const val AR_BOTTOM_FADE = 0.6f

// EDITABLE: Distance (in dp) the "Sign video (placeholder)" label sits above the bottom edge of the
// video box. Raised so it does not overlap the TRY button.
private val VIDEO_LABEL_BOTTOM_PADDING = 72.dp

/**
 * PURPOSE: Lesson details screen (wireframe: "lesson 1 - Greetings", big video placeholder,
 * sign name and description).
 * FUNCTION: Shows one lesson. Previous/Next lesson buttons switch to the neighboring lesson
 * without opening new screens.
 *
 * NAVIGATION: [onBack] returns to the Tutorials list; [onReturnToTranslation] jumps to Translation.
 * While AR mode is open, the Back button and the phone's Back gesture close AR mode first
 * (returning to the normal lesson page) instead of leaving the screen.
 *
 * @param initialLessonId Id of the lesson the user opened from the list.
 */
@Composable
fun LessonDetailScreen(
    initialLessonId: Int,
    onBack: () -> Unit,
    onReturnToTranslation: () -> Unit
) {
    // PURPOSE: Position of the lesson currently displayed in SampleData.lessons.
    var lessonPosition by rememberSaveable {
        mutableStateOf(SampleData.lessons.indexOfFirst { it.id == initialLessonId }.coerceAtLeast(0))
    }
    // PURPOSE: Whether the "video not available yet" pop-up is visible.
    var showVideoDialog by remember { mutableStateOf(false) }

    // PURPOSE: Whether the AR experience is currently open on this lesson page.
    var isArMode by rememberSaveable { mutableStateOf(false) }

    // ==========================================
    // SCREEN TRANSITION ANIMATION
    // EDIT HERE: arProgress goes 0f (normal lesson page) -> 1f (AR mode) and back.
    // Change the easing (e.g. LinearEasing) or AR_TRANSITION_DURATION_MS to change the feel.
    // Everything that moves (camera panel, preview, text fade) is driven by this one value.
    // ==========================================
    val arProgress by animateFloatAsState(
        targetValue = if (isArMode) 1f else 0f,
        animationSpec = tween(durationMillis = AR_TRANSITION_DURATION_MS, easing = FastOutSlowInEasing),
        label = "arTransition"
    )

    // ==========================================
    // TRY BUTTON ACTION
    // EDIT HERE: Modify what happens when the user enters / leaves the AR experience.
    // FUNCTION: openAr is called by the TRY button; closeAr by the Back button / Back gesture.
    // Future development: start the camera and the sign recognition in openAr, stop them in closeAr.
    // ==========================================
    val openAr: () -> Unit = { isArMode = true }
    val closeAr: () -> Unit = { isArMode = false }

    // NAVIGATION: Phone Back gesture/button closes AR mode first. When AR is closed this handler is
    // disabled, so Back behaves exactly as before (returns to the Tutorials list).
    BackHandler(enabled = isArMode) { closeAr() }

    // PURPOSE: Screen position of the video box (used as the start of the AR transition).
    var videoBoundsInRoot by remember { mutableStateOf(Rect.Zero) }
    // PURPOSE: Screen position of this whole screen container (used to convert the bounds above).
    var containerOrigin by remember { mutableStateOf(Offset.Zero) }

    // PURPOSE: Fades the description and Previous/Next row while AR mode is open (wireframe: faded text).
    val arBottomAlpha = 1f - AR_BOTTOM_FADE * arProgress
    // PURPOSE: Title text only turns white once the camera panel has grown behind it (avoids white-on-white).
    // EDITABLE: Change 0.55f / 0.45f to make the title change color earlier or later.
    val arTitleProgress = ((arProgress - 0.55f) / 0.45f).coerceIn(0f, 1f)

    val lesson = SampleData.lessons[lessonPosition]

    // PURPOSE: Root container. It holds (back to front): AR camera panel, the original lesson page,
    // and the AR reference preview. The page background moved here from the Column (same color).
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .onGloballyPositioned { containerOrigin = it.positionInRoot() }
    ) {
        // ==========================================
        // AR INTERFACE DESIGN
        // EDIT HERE: The AR camera panel (gray area growing out of the video box).
        // Layout and colors: components/ArSignPhraseOverlay.kt. Drawn BEHIND the lesson page so the
        // title and Back button stay visible on top of it.
        // ==========================================
        if (arProgress > 0f) {
            ArCameraPanel(
                progress = arProgress,
                startBounds = videoBoundsInRoot.translate(-containerOrigin)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // NAVIGATION: In AR mode the Back button closes AR (returns to the lesson page).
            LearningTopBar(
                onBack = if (isArMode) closeAr else onBack,
                onReturnToTranslation = onReturnToTranslation
            )
            Spacer(Modifier.height(12.dp))

            Text(
                "Lesson ${lesson.id}",
                style = MaterialTheme.typography.headlineMedium,
                color = lerp(PrimarySlate, AR_TITLE_COLOR, arTitleProgress)
            )
            Text(
                lesson.category,
                style = MaterialTheme.typography.titleMedium,
                color = lerp(TextMuted, AR_TITLE_COLOR, arTitleProgress)
            )
            Spacer(Modifier.height(12.dp))

            // ---- Video placeholder with a play button ----
            // PLACEHOLDER: Future development plays the local MP4 tutorial video here.
            // FUNCTION: This box is also where the AR transition starts: it fades out while the AR
            // camera panel grows from exactly this position.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .onGloballyPositioned { videoBoundsInRoot = it.boundsInRoot() }
                    .graphicsLayer { alpha = 1f - arProgress }
            ) {
                PlaceholderBox(
                    modifier = Modifier.fillMaxSize(),
                    icon = Icons.Filled.Videocam
                )
                RoundActionButton(
                    icon = Icons.Filled.PlayArrow,
                    contentDescription = "Play lesson video",
                    onClick = { showVideoDialog = true },
                    size = 64.dp,
                    enabled = !isArMode,
                    modifier = Modifier.align(Alignment.Center)
                )
                Text(
                    text = "Sign video (placeholder)",
                    color = TextMuted,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = VIDEO_LABEL_BOTTOM_PADDING)
                )

                // ==========================================
                // TRY BUTTON ACTION
                // EDIT HERE: The TRY button (wireframe: bottom-center of the video frame).
                // Text/shape/size/color: PrimaryPillButton in components/Buttons.kt.
                // Position: the align/padding below. What it does: openAr above.
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

            // ---- Sign name and description ----
            Column(
                modifier = Modifier
                    .alpha(arBottomAlpha)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(lesson.signName, style = MaterialTheme.typography.titleLarge, color = PrimarySlate)
                Spacer(Modifier.height(4.dp))
                Text(lesson.description, style = MaterialTheme.typography.bodyMedium, color = TextMuted)
            }
            Spacer(Modifier.height(12.dp))

            // ---- Previous / Next lesson ----
            // NOTE: Both buttons are disabled and faded while AR mode is open (wireframe: faded "Next").
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(arBottomAlpha),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // FUNCTION: Shows the previous lesson. Disabled on the first lesson.
                SecondaryPillButton(
                    text = "Previous lesson",
                    onClick = { if (lessonPosition > 0) lessonPosition-- },
                    enabled = lessonPosition > 0 && !isArMode
                )
                // FUNCTION: Shows the next lesson. Disabled on the last lesson.
                PrimaryPillButton(
                    text = "Next lesson",
                    onClick = { if (lessonPosition < SampleData.lessons.lastIndex) lessonPosition++ },
                    enabled = lessonPosition < SampleData.lessons.lastIndex && !isArMode
                )
            }
            Spacer(Modifier.height(8.dp))
        }

        // ==========================================
        // AR INTERFACE DESIGN
        // EDIT HERE: Sign-reference preview at the top-right (slides in from the right in AR mode).
        // Size/position/icon: components/ArSignPhraseOverlay.kt (ArReferencePreview).
        // Future: this is where the sample sign video for the current phrase (lesson.signName) is shown.
        // ==========================================
        ArReferencePreview(progress = arProgress)

        // ==========================================
        // AR FUNCTIONALITY  /  FUTURE CUSTOMIZATION
        // EDIT HERE: Further AR features can be added as extra layers in this Box, above the camera
        // panel, for example:
        //  - live camera (CameraX) inside ArCameraPanel,
        //  - hand/sign recognition results and feedback ("Correct!" / "Try again"),
        //  - a per-lesson reference video (load it using `lesson` above, so new phrases added to
        //    SampleData.lessons work automatically),
        //  - API/AI calls: run them from openAr/closeAr and pass results into these layers.
        // ==========================================
    }

    if (showVideoDialog) {
        PlaceholderDialog(
            title = "Video (placeholder)",
            message = "Tutorial videos will be added in a later stage of development.",
            onDismiss = { showVideoDialog = false }
        )
    }
}
