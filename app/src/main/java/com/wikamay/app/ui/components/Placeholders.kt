package com.wikamay.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wikamay.app.ui.theme.CameraBackground
import com.wikamay.app.ui.theme.PrimarySlate
import com.wikamay.app.ui.theme.ScanBracket
import com.wikamay.app.ui.theme.SoftBlueGray
import com.wikamay.app.ui.theme.TextMuted

/** FUNCTION: Draws the two diagonal lines (an "X") used for image placeholders in the wireframe. */
private fun DrawScope.drawCross(color: Color) {
    drawLine(color, Offset.Zero, Offset(size.width, size.height), strokeWidth = 2f)
    drawLine(color, Offset(size.width, 0f), Offset(0f, size.height), strokeWidth = 2f)
}

/**
 * PURPOSE: A labeled box that stands in for an image, avatar, or video that does not exist yet.
 * PLACEHOLDER: Replace the usage of this component with a real Image/Video when assets are ready.
 * EDITABLE: [label] is the text shown inside; [icon] is the optional icon; [showCross] toggles the X lines.
 */
@Composable
fun PlaceholderBox(
    modifier: Modifier = Modifier,
    label: String = "",
    icon: ImageVector? = null,
    showCross: Boolean = true,
    shape: Shape = RoundedCornerShape(12.dp)
) {
    val crossColor = PrimarySlate.copy(alpha = 0.25f)
    Box(
        modifier = modifier
            .clip(shape)
            .background(SoftBlueGray)
            .border(2.dp, PrimarySlate, shape)
            .drawBehind { if (showCross) drawCross(crossColor) },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = PrimarySlate, modifier = Modifier.size(40.dp))
            }
            if (label.isNotBlank()) {
                Text(
                    text = label,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(8.dp)
                        .background(SoftBlueGray)
                        .padding(horizontal = 4.dp)
                )
            }
        }
    }
}

/**
 * PURPOSE: A fake camera preview (light gray area with an X), used instead of a real camera.
 * PLACEHOLDER: Future development replaces this with the CameraX live preview.
 * The [content] slot lets screens draw things on top (scan frame, buttons, ...).
 */
@Composable
fun CameraPlaceholder(
    modifier: Modifier = Modifier,
    label: String? = null,
    shape: Shape = RoundedCornerShape(0.dp),
    content: @Composable BoxScope.() -> Unit = {}
) {
    val crossColor = ScanBracket.copy(alpha = 0.3f)
    Box(
        modifier = modifier
            .clip(shape)
            .background(CameraBackground)
            .drawBehind { drawCross(crossColor) }
    ) {
        if (label != null) {
            Text(
                text = label,
                color = TextMuted,
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(CameraBackground)
                    .padding(horizontal = 6.dp)
            )
        }
        content()
    }
}

/**
 * PURPOSE: The four corner brackets that frame where the user's hands should be (scanning guide).
 * EDITABLE: Colors come from ScanBracket in Color.kt.
 */
@Composable
fun ScanFrame(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = 6.dp.toPx()
        val half = strokeWidth / 2f
        val arm = size.minDimension * 0.18f

        // FUNCTION: Draws one "L" shaped corner. dx/dy (+1 or -1) choose which way the arms point.
        fun corner(x: Float, y: Float, dx: Float, dy: Float) {
            drawLine(ScanBracket, Offset(x, y), Offset(x + dx * arm, y), strokeWidth, StrokeCap.Round)
            drawLine(ScanBracket, Offset(x, y), Offset(x, y + dy * arm), strokeWidth, StrokeCap.Round)
        }
        corner(half, half, 1f, 1f)                              // top-left
        corner(size.width - half, half, -1f, 1f)                // top-right
        corner(half, size.height - half, 1f, -1f)               // bottom-left
        corner(size.width - half, size.height - half, -1f, -1f) // bottom-right
    }
}
