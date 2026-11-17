
package com.wikamay.app.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.camera.core.CameraSelector
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

// ==========================================
// AR INTERFACE DESIGN
// ==========================================

private val AR_PREVIEW_WIDTH = 100.dp
private const val AR_PREVIEW_ASPECT = 3f / 4f
private val AR_SCAN_FRAME_MARGIN = 12.dp
private val AR_PANEL_START_CORNER = 12.dp

// Animation blend
private fun mix(
    start: Float,
    stop: Float,
    progress: Float
): Float {
    return start + (stop - start) * progress
}

// ==========================================
// AR CAMERA PANEL
// ==========================================

@Composable
fun ArCameraPanel(
    progress: Float,
    startBounds: Rect,
    isFrontCamera: Boolean,
    onSwitchCamera: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val density = LocalDensity.current
        val containerWidthPx = constraints.maxWidth.toFloat()

        // Animated panel boundaries
        val left = mix(startBounds.left, 0f, progress)
        val top = mix(startBounds.top, 0f, progress)
        val right = mix(startBounds.right, containerWidthPx, progress)
        val bottom = startBounds.bottom

        val panelWidth = with(density) {
            (right - left).coerceAtLeast(0f).toDp()
        }

        val panelHeight = with(density) {
            (bottom - top).coerceAtLeast(0f).toDp()
        }

        // ==========================================
        // LIVE CAMERAX PREVIEW + CAMERA SWITCH
        // ==========================================

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        left.roundToInt(),
                        top.roundToInt()
                    )
                }
                .size(
                    width = panelWidth,
                    height = panelHeight
                )
        ) {

            // CAMERA PREVIEW
            CameraXPreview(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(
                    AR_PANEL_START_CORNER * (1f - progress)
                ),
                lensFacing = if (isFrontCamera) {
                    CameraSelector.LENS_FACING_FRONT
                } else {
                    CameraSelector.LENS_FACING_BACK
                }
            )

            // CAMERA SWITCH BUTTON - CENTER
            if (progress > 0.9f) {
                SquareIconButton(
                    icon = Icons.Filled.Cameraswitch,
                    contentDescription = "Switch camera",
                    onClick = onSwitchCamera,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = 0.dp, y = 300.dp)
                )
            }
        }

        // ==========================================
        // SCAN FRAME
        // ==========================================

        ScanFrame(
            modifier = Modifier
                .offset {
                    IntOffset(
                        startBounds.left.roundToInt(),
                        startBounds.top.roundToInt()
                    )
                }
                .size(
                    width = with(density) {
                        startBounds.width.toDp()
                    },
                    height = with(density) {
                        startBounds.height.toDp()
                    }
                )
                .padding(AR_SCAN_FRAME_MARGIN)
                .alpha(progress)
        )
    }
}

// ==========================================
// AR REFERENCE PREVIEW
// ==========================================

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
            .padding(
                horizontal = 20.dp,
                vertical = 12.dp
            ),
        contentAlignment = Alignment.TopEnd
    ) {
        PlaceholderBox(
            modifier = Modifier
                .offset {
                    IntOffset(
                        (
                                (1f - progress) *
                                        (AR_PREVIEW_WIDTH + 20.dp).toPx()
                                ).roundToInt(),
                        0
                    )
                }
                .width(AR_PREVIEW_WIDTH)
                .aspectRatio(AR_PREVIEW_ASPECT)
                .pointerInput(Unit) {
                    detectTapGestures { }
                },
            icon = Icons.Filled.Videocam,
            showCross = false,
            shape = RoundedCornerShape(8.dp)
        )
    }
}
