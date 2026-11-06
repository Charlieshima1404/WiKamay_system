package com.wikamay.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wikamay.app.ui.components.LearningCard
import com.wikamay.app.ui.components.LearningTopBar
import com.wikamay.app.ui.components.PlaceholderBox
import com.wikamay.app.ui.components.PrimaryPillButton
import com.wikamay.app.ui.theme.PrimarySlate
import com.wikamay.app.ui.theme.TextMuted

// EDITABLE: Texts used on this screen.
private const val MODULE_TITLE = "FSL Learning Module"
private const val MODULE_DESCRIPTION =
    "Practice the FSL alphabet and numbers, or follow short tutorial lessons to learn basic signs."

/**
 * PURPOSE: The LEARNING MENU. It opens from the upper-right button of both translation screens.
 * It links to the three learning sections: Alphabet (TRY button), Numbers, and Tutorials.
 *
 * NAVIGATION:
 * - [onBack]: the top-left button returns to the Translation screen the user came from.
 * - [onOpenAlphabet], [onOpenNumbers], [onOpenTutorials]: open each learning section.
 */
@Composable
fun LearningHomeScreen(
    onBack: () -> Unit,
    onOpenAlphabet: () -> Unit,
    onOpenNumbers: () -> Unit,
    onOpenTutorials: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        LearningTopBar(onBack = onBack)
        Spacer(Modifier.height(16.dp))

        Text(MODULE_TITLE, style = MaterialTheme.typography.headlineMedium, color = PrimarySlate)
        Spacer(Modifier.height(4.dp))
        Text(MODULE_DESCRIPTION, style = MaterialTheme.typography.bodyMedium, color = TextMuted)
        Spacer(Modifier.height(24.dp))

        // ---- FSL Alphabet section (wireframe: "Text-to-FSL AR Alphabet Overlay" + TRY) ----
        Text("Text-to-FSL AR Alphabet Overlay", style = MaterialTheme.typography.titleMedium, color = PrimarySlate)
        Spacer(Modifier.height(8.dp))
        // PLACEHOLDER: Future development shows a preview of the AR alphabet overlay here.
        PlaceholderBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            label = "AR alphabet overlay preview"
        )
        Spacer(Modifier.height(12.dp))
        PrimaryPillButton(
            text = "TRY",
            onClick = onOpenAlphabet, // NAVIGATION: opens the letter selection screen.
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(24.dp))

        // ---- Learn FSL section ----
        Text("Learn FSL", style = MaterialTheme.typography.titleMedium, color = PrimarySlate)
        Spacer(Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            LearningCard(
                title = "Numbers",
                subtitle = "Practice the FSL number handshapes",
                icon = Icons.Filled.FormatListNumbered,
                onClick = onOpenNumbers // NAVIGATION: opens the number selection screen.
            )
            LearningCard(
                title = "Tutorials",
                subtitle = "Short lessons for basic FSL signs",
                icon = Icons.Filled.School,
                onClick = onOpenTutorials // NAVIGATION: opens the tutorial list.
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}
