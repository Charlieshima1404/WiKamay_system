package com.wikamay.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wikamay.app.ui.components.ModeToggle
import com.wikamay.app.ui.components.PlaceholderBox
import com.wikamay.app.ui.components.PlaceholderDialog
import com.wikamay.app.ui.components.RoundActionButton
import com.wikamay.app.ui.components.SquareIconButton
import com.wikamay.app.ui.components.TranslationMode
import com.wikamay.app.ui.theme.PanelGray
import com.wikamay.app.ui.theme.PrimarySlate
import com.wikamay.app.ui.theme.TextDark
import com.wikamay.app.ui.theme.TextMuted
import com.wikamay.app.ui.theme.ToggleTrack
import java.util.Locale

// EDITABLE: Texts used on this screen.
private const val INPUT_HINT = "Type a word or phrase..."
private const val AVATAR_LABEL = "Virtual signing avatar\n(placeholder)"

@Composable
fun TextToSignScreen(
    onSwitchToSignToText: () -> Unit,
    onOpenLearningMenu: () -> Unit,
    showTutorial: Boolean = false,
    onTutorialFinished: () -> Unit = {},
    onTutorialDismissed: () -> Unit = onTutorialFinished
) {
    // Tutorial element positions
    var avatarBounds by remember { mutableStateOf<Rect?>(null) }
    var speedBounds by remember { mutableStateOf<Rect?>(null) }
    var inputBounds by remember { mutableStateOf<Rect?>(null) }
    var learningShortcutBounds by remember { mutableStateOf<Rect?>(null) }
    var modeToggleBounds by remember { mutableStateOf<Rect?>(null) }

    // Text input
    var inputText by rememberSaveable { mutableStateOf("") }

    // Signing speed
    var signingSpeed by rememberSaveable { mutableStateOf(1.0f) }

    // Dialog visibility
    var showMicDialog by remember { mutableStateOf(false) }

    // Help button: temporarily displays the tutorial again
    var showHelpTutorial by remember { mutableStateOf(false) }

    // Tutorial display state
    val tutorialVisible = showTutorial || showHelpTutorial

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .systemBarsPadding()
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {

            // ---- Top row: Help (left) and Learning Menu (right) ----
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // HELP BUTTON (top-left, yellow)
                IconButton(
                    onClick = {
                        showHelpTutorial = true
                    },
                    modifier = Modifier
                        .background(
                            Color(0xFFFFC107),
                            RoundedCornerShape(10.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Help,
                        contentDescription = "Show tutorial",
                        tint = Color.White
                    )
                }

                // LEARNING MENU BUTTON (top-right)
                SquareIconButton(
                    icon = Icons.Filled.School,
                    contentDescription = "Open Learning Menu",
                    onClick = onOpenLearningMenu,
                    modifier = Modifier.onGloballyPositioned {
                        learningShortcutBounds = it.boundsInRoot()
                    }
                )
            }

            Spacer(Modifier.height(4.dp))

            // ---- Translation direction toggle ----
            ModeToggle(
                selected = TranslationMode.TEXT_TO_SIGN,
                onModeSelected = { mode ->
                    if (mode == TranslationMode.SIGN_TO_TEXT) {
                        onSwitchToSignToText()
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(280.dp)
                    .onGloballyPositioned {
                        modeToggleBounds = it.boundsInRoot()
                    }
            )

            Spacer(Modifier.height(16.dp))

            // ---- Virtual signing avatar area ----
            PlaceholderBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .onGloballyPositioned {
                        avatarBounds = it.boundsInRoot()
                    },
                label = if (inputText.isBlank()) {
                    AVATAR_LABEL
                } else {
                    "Avatar will sign:\n\"$inputText\""
                },
                icon = Icons.Filled.Person
            )

            Spacer(Modifier.height(8.dp))

            // ---- Signing speed control ----
            Row(
                modifier = Modifier.onGloballyPositioned {
                    speedBounds = it.boundsInRoot()
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "0.5x",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )

                Slider(
                    value = signingSpeed,
                    onValueChange = { signingSpeed = it },
                    valueRange = 0.5f..1.5f,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = PrimarySlate,
                        activeTrackColor = PrimarySlate,
                        inactiveTrackColor = ToggleTrack
                    )
                )

                Text(
                    "1.5x",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            }

            Text(
                text = "Signing speed: " +
                        String.format(Locale.US, "%.1f", signingSpeed) + "x",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(8.dp))

            // ---- Text input panel ----
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 150.dp)
                    .onGloballyPositioned {
                        inputBounds = it.boundsInRoot()
                    }
                    .clip(RoundedCornerShape(16.dp))
                    .background(PanelGray)
                    .padding(12.dp)
            ) {

                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text(INPUT_HINT) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 72.dp, max = 140.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark
                    ),
                    trailingIcon = {
                        if (inputText.isNotEmpty()) {
                            IconButton(
                                onClick = { inputText = "" }
                            ) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = "Clear text",
                                    tint = PrimarySlate
                                )
                            }
                        }
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    // Placeholder microphone button
                    RoundActionButton(
                        icon = Icons.Filled.Mic,
                        contentDescription = "Speak (microphone)",
                        onClick = { showMicDialog = true },
                        size = 48.dp
                    )
                }
            }
        }

        // ---- TEXT -> SIGN TUTORIAL ----
        if (tutorialVisible) {
            TextToSignTutorial(
                avatarBounds = avatarBounds,
                speedBounds = speedBounds,
                inputBounds = inputBounds,
                learningShortcutBounds = learningShortcutBounds,
                modeToggleBounds = modeToggleBounds,
                onFinish = {
                    showHelpTutorial = false
                    onTutorialFinished()
                },
                onDismiss = {
                    showHelpTutorial = false
                    onTutorialDismissed()
                }
            )
        }
    }

    // ---- Microphone placeholder dialog ----
    if (showMicDialog) {
        PlaceholderDialog(
            title = "Speech input (placeholder)",
            message = "Speech recognition will be added in a later stage of development. For now, please type your text in the box.",
            onDismiss = {
                showMicDialog = false
            }
        )
    }
}