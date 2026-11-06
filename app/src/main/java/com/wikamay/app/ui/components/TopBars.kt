package com.wikamay.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Translate
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * PURPOSE: The top row shared by all learning screens.
 * - Left square button = Back (one step back).
 * - Right square button (optional) = jump straight back to the Translation screen.
 * NAVIGATION: Both actions are provided by the NavGraph through [onBack] and [onReturnToTranslation].
 */
@Composable
fun LearningTopBar(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onReturnToTranslation: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SquareIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            onClick = onBack
        )
        if (onReturnToTranslation != null) {
            SquareIconButton(
                icon = Icons.Filled.Translate,
                contentDescription = "Return to translation screen",
                onClick = onReturnToTranslation
            )
        }
    }
}
