package com.wikamay.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.PanTool
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wikamay.app.ui.components.CameraPlaceholder
import com.wikamay.app.ui.components.LearningTopBar
import com.wikamay.app.ui.components.PlaceholderBox
import com.wikamay.app.ui.components.PlaceholderDialog
import com.wikamay.app.ui.components.PrimaryPillButton
import com.wikamay.app.ui.components.RoundActionButton
import com.wikamay.app.ui.components.ScanFrame
import com.wikamay.app.ui.theme.PrimarySlate
import com.wikamay.app.ui.theme.TextMuted

/**
 * PURPOSE: Practice screen shared by the FSL Alphabet and Numbers sections
 * (wireframe: "Align your hand to the overlay" with a camera area, corner brackets and a
 * reference box at the top).
 *
 * FUNCTION: Shows the selected letter/number, a reference handshape placeholder, a practice area
 * (fake camera), and Previous/Next buttons that change the displayed item.
 *
 * NAVIGATION: [onBack] returns to the selection grid; [onReturnToTranslation] jumps to Translation.
 *
 * @param itemKind "Letter" or "Number" (only used for labels).
 * @param startIndex Position of the item the user tapped on the selection grid.
 */
@Composable
fun PracticeScreen(
    screenTitle: String,
    itemKind: String,
    items: List<String>,
    startIndex: Int,
    onBack: () -> Unit,
    onReturnToTranslation: () -> Unit
) {
    // PURPOSE: Position of the item currently displayed. Changes when Previous/Next are tapped.
    var currentIndex by rememberSaveable { mutableStateOf(startIndex.coerceIn(0, items.lastIndex)) }

    // PURPOSE: Whether the "checking is not available yet" pop-up is visible.
    var showCheckDialog by remember { mutableStateOf(false) }

    // PURPOSE: The letter/number shown right now.
    val currentItem = items[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        LearningTopBar(onBack = onBack, onReturnToTranslation = onReturnToTranslation)
        Spacer(Modifier.height(8.dp))

        Text(
            text = "$screenTitle: $itemKind $currentItem",
            style = MaterialTheme.typography.titleLarge,
            color = PrimarySlate,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        // ---- Practice area: fake camera + scan frame + reference handshape ----
        // PLACEHOLDER: Future development shows the live camera with a 2D handshape overlay here.
        CameraPlaceholder(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            label = "Practice area\n(camera placeholder)",
            shape = RoundedCornerShape(16.dp)
        ) {
            ScanFrame(modifier = Modifier
                .fillMaxSize()
                .padding(24.dp))
            // PLACEHOLDER: Replace with the real reference handshape image for the selected item.
            PlaceholderBox(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 28.dp)
                    .size(width = 150.dp, height = 90.dp),
                label = "Handshape: $currentItem",
                icon = Icons.Filled.PanTool,
                showCross = false
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Align your hand to the overlay",
            style = MaterialTheme.typography.bodyLarge,
            color = TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        // ---- Previous / current item / Next ----
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // FUNCTION: Goes to the previous item. Disabled on the first item.
            RoundActionButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Previous $itemKind",
                onClick = { if (currentIndex > 0) currentIndex-- },
                enabled = currentIndex > 0
            )
            // PURPOSE: Large display of the selected letter/number.
            Text(
                text = currentItem,
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = PrimarySlate
            )
            // FUNCTION: Goes to the next item. Disabled on the last item.
            RoundActionButton(
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next $itemKind",
                onClick = { if (currentIndex < items.lastIndex) currentIndex++ },
                enabled = currentIndex < items.lastIndex
            )
        }
        Text(
            text = "${currentIndex + 1} of ${items.size}",
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        // PLACEHOLDER: Future development compares the user's hand with the reference handshape.
        PrimaryPillButton(
            text = "Check my sign",
            onClick = { showCheckDialog = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(8.dp))
    }

    if (showCheckDialog) {
        PlaceholderDialog(
            title = "Handshape check (placeholder)",
            message = "Camera hand tracking and handshape comparison will be implemented in a later stage of development.",
            onDismiss = { showCheckDialog = false }
        )
    }
}
