
package com.wikamay.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
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
import com.wikamay.app.ui.theme.PrimarySlate
import com.wikamay.app.ui.theme.ScanBracket
import com.wikamay.app.ui.theme.SoftBlueGray
import com.wikamay.app.ui.theme.TextMuted

/**
 * Draws the two diagonal lines (an X) used for image placeholders.
 */
private fun DrawScope.drawCross(color: Color) {
    drawLine(
        color = color,
        start = Offset.Zero,
        end = Offset(size.width, size.height),
        strokeWidth = 2f
    )

    drawLine(
        color = color,
        start = Offset(size.width, 0f),
        end = Offset(0f, size.height),
        strokeWidth = 2f
    )
}

/**
 * A labeled box used as a placeholder for images, avatars, or videos.
 */
@Composable
fun PlaceholderBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    label: String = "",
    icon: ImageVector? = null,
    showCross: Boolean = true
) {
    val crossColor = PrimarySlate.copy(alpha = 0.25f)

    Box(
        modifier = modifier
            .clip(shape)
            .background(SoftBlueGray)
            .border(2.dp, PrimarySlate, shape)
            .drawBehind {
                if (showCross) {
                    drawCross(crossColor)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimarySlate,
                    modifier = Modifier.size(40.dp)
                )
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
 * Alias for [PlaceholderBox].
 */
@Composable
fun Placeholder(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    label: String = "",
    icon: ImageVector? = null,
    showCross: Boolean = true
) {
    PlaceholderBox(
        modifier = modifier,
        shape = shape,
        label = label,
        icon = icon,
        showCross = showCross
    )
}

/**
 * Draws four corner brackets to guide the user's hand placement.
 */
@Composable
fun ScanFrame(
    modifier: Modifier = Modifier,
    isCorrect: Boolean = false
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 6.dp.toPx()

        val bracketColor = if (isCorrect) {
            Color.Green
        } else {
            ScanBracket
        }

        val half = strokeWidth / 2f
        val arm = size.minDimension * 0.18f

        // Draws one L-shaped corner.
        fun corner(
            x: Float,
            y: Float,
            dx: Float,
            dy: Float
        ) {
            drawLine(
                color = bracketColor,
                start = Offset(x, y),
                end = Offset(x + dx * arm, y),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            drawLine(
                color = bracketColor,
                start = Offset(x, y),
                end = Offset(x, y + dy * arm),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }

        // Top-left
        corner(half, half, 1f, 1f)

        // Top-right
        corner(size.width - half, half, -1f, 1f)

        // Bottom-left
        corner(half, size.height - half, 1f, -1f)

        // Bottom-right
        corner(
            size.width - half,
            size.height - half,
            -1f,
            -1f
        )
    }
}
