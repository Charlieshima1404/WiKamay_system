package com.wikamay.app.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

// ==========================================
// AR INTERFACE DESIGN
// EDIT HERE: Sizes used by the AR layout.
// (Colors come from ui/theme/Color.kt:
//  CameraBackground = AR panel color, ScanBracket = corner bracket color.)
// ==========================================

// EDITABLE: Width of the small sign-reference preview (top-right). Height follows AR_PREVIEW_ASPECT.
private val AR_PREVIEW_WIDTH = 100.dp

// EDITABLE: Width / height ratio of the reference preview (3f / 4f = portrait).
private const val AR_PREVIEW_ASPECT = 3f / 4f

// EDITABLE: Gap between the original video box edge and the scan-frame corner brackets.
private val AR_SCAN_FRAME_MARGIN = 12.dp

// EDITABLE: Corner radius of the AR camera panel at the start of the transition
// (must match the video placeholder's rounded corner, 12.dp, so the hand-off looks seamless).
private val AR_PANEL_START_CORNER = 12.dp

// PURPOSE: Simple linear blend between two values (progress 0f = start, 1f = stop).
private fun mix(start: Float, stop: Float, progress: Float): Float = start + (stop - start) * progress

/**
 * PURPOSE: The AR camera area. It starts exactly on top of the lesson's video box ([startBounds])
 * and GROWS upward and sideways until it fills the top of the screen (behind the lesson title
 * and the Back button), as shown in the second wireframe screen.
 *
 * ==========================================
 * SCREEN TRANSITION ANIMATION
 * EDIT HERE: The motion is driven by [progress] (0f = lesson page, 1f = AR mode), which comes from
 * the animation in LessonDetailScreen.kt (see AR_TRANSITION_DURATION_MS). To change WHERE the panel
 * grows to, edit the left/top/right/bottom lines below
 * (for example, set `bottom` to grow downward too).
 * ==========================================
 *
 * @param progress    Animation progress, 0f..1f.
 * @param startBounds Position and size of the lesson video box, relative to the screen container.
 */
@Composable
fun ArCameraPanel(
    progress: Float,
    startBounds: Rect,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val containerWidthPx = with(density) { maxWidth.toPx() }

        // FUNCTION: Edges of the growing panel. Left/top/right move to the screen edges;
        // bottom stays on the bottom edge of the original video box (wireframe: text stays below).
        val left = mix(startBounds.left, 0f, progress)
        val top = mix(startBounds.top, 0f, progress)
        val right = mix(startBounds.right, containerWidthPx, progress)
        val bottom = startBounds.bottom
        val panelWidth = with(density) { (right - left).coerceAtLeast(0f).toDp() }
        val panelHeight = with(density) { (bottom - top).coerceAtLeast(0f).toDp() }

        // ==========================================
        // AR FUNCTIONALITY
        // EDIT HERE: This is the AR camera surface.
        // PLACEHOLDER: CameraPlaceholder is the app's existing fake camera preview.
        // Future development: replace it with a CameraX PreviewView (AndroidView) to show the
        // live camera, and run hand/sign recognition (e.g. MediaPipe Hands or a TFLite model)
        // on its frames. Requires the CAMERA permission (see the notes delivered with this change).
        // ==========================================
        Box(
            modifier = Modifier
                .offset { IntOffset(left.roundToInt(), top.roundToInt()) }
                .size(width = panelWidth, height = panelHeight)
        ) {
            CameraPlaceholder(
                modifier = Modifier.fillMaxSize(),
                // UI: corners round off from 12.dp to square as the panel reaches the screen edges.
                shape = RoundedCornerShape(AR_PANEL_START_CORNER * (1f - progress))
            )
        }

        // UI: Scan frame (four corner brackets) over the original video box area, fading in with the panel.
        // EDIT HERE: Change AR_SCAN_FRAME_MARGIN above to move the brackets closer or farther from the edges.
        // Bracket color / thickness: ScanBracket in ui/theme/Color.kt and ScanFrame in Placeholders.kt.
        ScanFrame(
            modifier = Modifier
                .offset { IntOffset(startBounds.left.roundToInt(), startBounds.top.roundToInt()) }
                .size(
                    width = with(density) { startBounds.width.toDp() },
                    height = with(density) { startBounds.height.toDp() }
                )
                .padding(AR_SCAN_FRAME_MARGIN)
                .alpha(progress)
        )
    }
}

/**
 * PURPOSE: The small white preview box at the top-right of the AR screen (wireframe, second screen).
 * It is meant to show the reference sign (how the phrase is signed) while the user practices.
 * It slides in from the right edge as [progress] goes from 0f to 1f, and covers the lesson page's
 * top-right "return to translation" button while AR mode is open.
 *
 * ==========================================
 * AR INTERFACE DESIGN
 * EDIT HERE: Size -> AR_PREVIEW_WIDTH / AR_PREVIEW_ASPECT above.
 * Position -> the padding on the outer Box (matches the lesson page's 20.dp side margin and 12.dp top margin).
 * Icon -> the `icon` argument. Corner roundness -> the `shape` argument.
 * ==========================================
 *
 * PLACEHOLDER: Future development plays the local sign reference video for the current phrase here
 * (for example with Media3 ExoPlayer from a raw/asset MP4).
 */
@Composable
fun ArReferencePreview(
    progress: Float,
    modifier: Modifier = Modifier
) {
    if (progress <= 0f) return

    Box(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        PlaceholderBox(
            modifier = Modifier
                // UI: Slide in from the right edge (distance = preview width + side margin).
                .offset { IntOffset(((1f - progress) * (AR_PREVIEW_WIDTH + 20.dp).toPx()).roundToInt(), 0) }
                .width(AR_PREVIEW_WIDTH)
                .aspectRatio(AR_PREVIEW_ASPECT)
                // FUNCTION: Swallows taps so they do not reach the button hidden underneath.
                .pointerInput(Unit) { detectTapGestures { } },
            icon = Icons.Filled.Videocam,
            showCross = false,
            shape = RoundedCornerShape(8.dp)
        )
    }
}
