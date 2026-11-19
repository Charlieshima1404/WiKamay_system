package com.wikamay.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

private const val HAND_SCALE = 1.2f

@Composable
fun HandOverlay(
    handLandmarkPoints: List<List<Offset>>,
    cameraRotationDegrees: Int,
    isFrontCamera: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {

        val connections = listOf(
            0 to 1, 1 to 2, 2 to 3, 3 to 4,
            0 to 5, 5 to 6, 6 to 7, 7 to 8,
            5 to 9, 9 to 10, 10 to 11, 11 to 12,
            9 to 13, 13 to 14, 14 to 15, 15 to 16,
            13 to 17, 17 to 18, 18 to 19, 19 to 20,
            0 to 17
        )

        fun mapPoint(x: Float, y: Float): Offset {
            val rotatedPoint = when (cameraRotationDegrees) {
                0 -> x to y
                90 -> (1f - y) to x
                180 -> (1f - x) to (1f - y)
                270 -> y to (1f - x)
                else -> x to y
            }

            val normalizedX = if (isFrontCamera) {
                1f - rotatedPoint.first
            } else {
                rotatedPoint.first
            }

            return Offset(
                x = (
                        0.5f + (normalizedX - 0.5f) * HAND_SCALE
                        ) * size.width,

                y = (
                        0.5f + (rotatedPoint.second - 0.5f) * HAND_SCALE
                        ) * size.height
            )
        }

        handLandmarkPoints.forEach { hand ->

            connections.forEach { (start, end) ->
                if (hand.size > maxOf(start, end)) {
                    drawLine(
                        color = Color.Green,
                        start = mapPoint(
                            hand[start].x,
                            hand[start].y
                        ),
                        end = mapPoint(
                            hand[end].x,
                            hand[end].y
                        ),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }

            hand.forEach { point ->
                drawCircle(
                    color = Color.Yellow,
                    radius = 5.dp.toPx(),
                    center = mapPoint(point.x, point.y)
                )
            }
        }
    }
}