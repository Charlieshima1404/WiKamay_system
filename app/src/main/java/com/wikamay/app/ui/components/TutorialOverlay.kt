package com.wikamay.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wikamay.app.ui.theme.PrimarySlate
import com.wikamay.app.ui.theme.TextDark
import com.wikamay.app.ui.theme.TextMuted

// ===== TUTORIAL NAVIGATION AND STEPS: WHAT ONE STEP CONTAINS =====
// A tutorial step = the element to highlight + the message to show.
// The steps themselves are listed where each tutorial is used:
//   Main Menu ............. ui/screens/MainMenuScreen.kt
//   Translator walkthroughs  ui/screens/TranslatorTutorials.kt  (Text -> Sign and Sign -> Text)
//   Learning Module Home .. ui/screens/LearningHomeScreen.kt
/**
 * PURPOSE: One step of the guided tutorial.
 * @param targetBounds Screen position of the element to highlight (from `onGloballyPositioned { it.boundsInRoot() }`).
 *                     When null the tooltip is shown in the middle of the screen without a highlight.
 * @param message      Text shown inside the tooltip.
 */
data class TutorialStep(
    val targetBounds: Rect?,
    val message: String
)

// ===== TRANSLATOR TUTORIAL UI: EDIT TOOLTIP AND HIGHLIGHT APPEARANCE HERE =====
// These values control how EVERY walkthrough looks (Translator, Main Menu and Learning Module):
//  - SCRIM_COLOR ........ the dark layer over the screen (change the alpha number to make it lighter/darker)
//  - HIGHLIGHT_PADDING .. space between the highlighted element and its dashed outline
//  - HIGHLIGHT_CORNER ... roundness of the highlighted cut-out and outline
//  - TOOLTIP_WIDTH ...... width of the white message box
//  - TOOLTIP_GAP ........ distance between the highlighted element and the message box
// More tooltip styling (box color, corner, text style, Next/OK button color) is in TutorialTooltip at the
// bottom of this file. The text of each step is NOT here: it lives next to the step list of each tutorial.
private val SCRIM_COLOR = Color.Black.copy(alpha = 0.6f)   // dark layer over the screen
private val HIGHLIGHT_PADDING = 8.dp                        // space between the element and its dashed outline
private val HIGHLIGHT_CORNER = 16.dp                        // roundness of the highlight cut-out
private val TOOLTIP_WIDTH = 240.dp
private val TOOLTIP_GAP = 16.dp                             // distance between the highlight and the tooltip

/**
 * PURPOSE: A guided walkthrough drawn over the current screen: the screen is dimmed, one element is
 * highlighted with a dashed outline, and a tooltip explains it with a Next / OK button and a step
 * counter (for example 1/3). The X button (bottom-right) closes the tutorial at any time.
 *
 * It only DRAWS over the screen and blocks taps on the screen underneath while it is visible.
 * It does not change what any button does.
 *
 * ===== TUTORIAL NAVIGATION AND STEPS: HOW THE PARAMETERS WORK =====
 * [steps]        The steps to show, in order (built by each screen, see TutorialStep above).
 * [onFinish]     Called when the user taps "OK" on the LAST step (the "Finish" action).
 * [onDismiss]    Called when the user closes the tutorial early with the X button (the "Skip" action).
 *                Defaults to [onFinish].
 * [onStepChange] Called whenever a step becomes active (used by the Learning Module Home to scroll the
 *                highlighted card into view).
 * What happens after Finish / Skip (which flags are saved) is decided in navigation/WiKamayNavGraph.kt.
 */
@Composable
fun TutorialOverlay(
    steps: List<TutorialStep>,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = onFinish,
    onStepChange: (Int) -> Unit = {}
) {
    if (steps.isEmpty()) return

    // ===== TUTORIAL NAVIGATION AND STEPS: CURRENT STEP =====
    // stepIndex = which step is showing now (0 = first). "Next" adds 1; the last step shows "OK" instead.
    // There is no "Back" button in this tutorial. To add one, decrease stepIndex in a new button
    // inside TutorialTooltip (bottom of this file).
    var stepIndex by remember { mutableStateOf(0) }
    var origin by remember { mutableStateOf(Offset.Zero) }
    val safeIndex = stepIndex.coerceIn(0, steps.lastIndex)
    val step = steps[safeIndex]
    val isLast = safeIndex == steps.lastIndex

    LaunchedEffect(safeIndex) { onStepChange(safeIndex) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { origin = it.positionInRoot() }
            // FUNCTION: Swallow taps so the screen underneath cannot be tapped during the tutorial.
            .pointerInput(Unit) { detectTapGestures { } }
    ) {
        val density = LocalDensity.current
        val target = step.targetBounds?.translate(-origin)

        // ===== TRANSLATOR TUTORIAL UI: HIGHLIGHT DRAWING =====
        // Draws the dark layer, cuts a rounded hole around the highlighted element, and draws the white
        // dashed outline. Dash length/gap: floatArrayOf(16f, 10f). Outline thickness: 2.dp.
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        ) {
            drawRect(SCRIM_COLOR)
            if (target != null) {
                val area = target.inflate(HIGHLIGHT_PADDING.toPx())
                val corner = CornerRadius(HIGHLIGHT_CORNER.toPx())
                drawRoundRect(
                    color = Color.Transparent,
                    topLeft = area.topLeft,
                    size = area.size,
                    cornerRadius = corner,
                    blendMode = BlendMode.Clear
                )
                drawRoundRect(
                    color = Color.White,
                    topLeft = area.topLeft,
                    size = area.size,
                    cornerRadius = corner,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 10f))
                    )
                )
            }
        }

        // ===== TRANSLATOR TUTORIAL UI: TOOLTIP POSITION AND BUTTON BEHAVIOR =====
        // The tooltip goes BELOW the highlighted element when it is in the upper half of the screen and
        // ABOVE it when it is in the lower half. The button below is the Next / Finish control:
        //   not the last step -> "Next" shows the next step
        //   last step         -> "OK" calls onFinish
        val tooltip: @Composable () -> Unit = {
            TutorialTooltip(
                message = step.message,
                counter = "${safeIndex + 1}/${steps.size}",
                buttonLabel = if (isLast) "OK" else "Next",
                onButton = { if (isLast) onFinish() else stepIndex = safeIndex + 1 }
            )
        }
        if (target == null) {
            Box(modifier = Modifier.align(Alignment.Center)) { tooltip() }
        } else {
            val tooltipX = with(density) { target.center.x.toDp() - TOOLTIP_WIDTH / 2 }
                .coerceIn(16.dp, maxWidth - TOOLTIP_WIDTH - 16.dp)
            val showBelow = target.center.y < with(density) { maxHeight.toPx() } / 2f
            if (showBelow) {
                // Element is in the upper half: tooltip goes under it.
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(
                            start = tooltipX,
                            top = with(density) { target.bottom.toDp() } + TOOLTIP_GAP + HIGHLIGHT_PADDING
                        )
                ) { tooltip() }
            } else {
                // Element is in the lower half: tooltip goes above it.
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(
                            start = tooltipX,
                            bottom = maxHeight - with(density) { target.top.toDp() } + TOOLTIP_GAP + HIGHLIGHT_PADDING
                        )
                ) { tooltip() }
            }
        }

        // ===== TUTORIAL NAVIGATION AND STEPS: SKIP / CLOSE (X) BUTTON =====
        // The round X button at the bottom-right closes the whole tutorial (calls onDismiss).
        // Position: .align(BottomEnd) + padding(20.dp). Size: size(40.dp). Color: Color.White.copy(alpha = 0.25f).
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(20.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.25f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Close, contentDescription = "Close tutorial", tint = Color.White)
        }
    }
}

// ===== TRANSLATOR TUTORIAL UI: EDIT TOOLTIP BOX DESIGN HERE =====
// The white message box: background color, corner roundness (12.dp), inner padding (14.dp), message text
// style (bodyMedium, TextDark), step counter (e.g. "1/3", 12.sp, bold) and the Next / OK button
// (PrimarySlate orange, 8.dp corners).
@Composable
private fun TutorialTooltip(
    message: String,
    counter: String,
    buttonLabel: String,
    onButton: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(TOOLTIP_WIDTH)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(14.dp)
    ) {
        Text(message, style = MaterialTheme.typography.bodyMedium, color = TextDark)
        Spacer(Modifier.size(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(counter, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onButton,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimarySlate, contentColor = Color.White),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 4.dp)
            ) { Text(buttonLabel) }
        }
    }
}
