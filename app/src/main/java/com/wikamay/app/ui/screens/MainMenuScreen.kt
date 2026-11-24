
package com.wikamay.app.ui.screens //MENUSCREEN AND ALSO HAVE THE TUTORIAL OVERLAY FOR THE MAIN MENU//

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wikamay.app.R
import com.wikamay.app.ui.components.TutorialOverlay
import com.wikamay.app.ui.components.TutorialStep
import com.wikamay.app.ui.theme.PrimarySlate

// ==========================================
// MAIN MENU TEXTS
// EDIT HERE: Texts shown on the Main Menu and in its tutorial.
// ==========================================

private const val MENU_TAGLINE =
    "Bridging communication through the power of Filipino Sign Language"

private const val TRANSLATOR_LABEL = "Translator"
private const val LEARNING_LABEL = "Learning Module"

private const val TUTORIAL_TRANSLATOR =
    "Translator: turn speech or text into Filipino Sign Language, or sign to your camera to get text and speech."

private const val TUTORIAL_LEARNING =
    "Learning Module: practice the FSL alphabet, numbers and everyday phrases."

/**
 * PURPOSE:
 * The MAIN MENU shown after onboarding and on every later launch.
 * It is the entry point to the two main parts of the app.
 *
 * NAVIGATION:
 * - onOpenTranslator: opens the Translation screen.
 * - onOpenLearning: opens the Learning Module.
 *
 * TUTORIAL:
 * When showTutorial is true, a guided walkthrough is drawn over the menu.
 */
@Composable
fun MainMenuScreen(
    onOpenTranslator: () -> Unit,
    onOpenLearning: () -> Unit,
    showTutorial: Boolean = false,
    onTutorialFinished: () -> Unit = {},
    onTutorialDismissed: () -> Unit = onTutorialFinished
) {

    // PURPOSE: Screen positions of the two buttons for tutorial highlighting.
    var translatorBounds by remember { mutableStateOf<Rect?>(null) }
    var learningBounds by remember { mutableStateOf<Rect?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WelcomeGradient)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center
        ) {

            // ---- LOGO ----
            Image(
                painter = painterResource(id = R.drawable.wikamay_logo),
                contentDescription = "WiKamay",
                modifier = Modifier.fillMaxWidth(0.8f),
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.CenterStart
            )

            Spacer(Modifier.height(8.dp))

            // ---- DIVIDER ----
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(2.dp)
                    .background(Color.White)
            )

            Spacer(Modifier.height(8.dp))

            // ---- TAGLINE ----
            Text(
                MENU_TAGLINE,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(Modifier.height(24.dp))

            // ---- TRANSLATOR BUTTON ----
            MenuPillButton(
                label = TRANSLATOR_LABEL,
                icon = Icons.Filled.Translate,
                onClick = onOpenTranslator,
                modifier = Modifier.onGloballyPositioned {
                    translatorBounds = it.boundsInRoot()
                }
            )

            Spacer(Modifier.height(12.dp))

            // ---- LEARNING MODULE BUTTON ----
            MenuPillButton(
                label = LEARNING_LABEL,
                icon = Icons.Filled.School,
                onClick = onOpenLearning,
                modifier = Modifier.onGloballyPositioned {
                    learningBounds = it.boundsInRoot()
                }
            )
        }

        // ==========================================
        // MAIN MENU TUTORIAL OVERLAY
        // ==========================================
        if (showTutorial) {
            TutorialOverlay(
                steps = listOf(
                    TutorialStep(translatorBounds, TUTORIAL_TRANSLATOR),
                    TutorialStep(learningBounds, TUTORIAL_LEARNING)
                ),
                onFinish = onTutorialFinished,
                onDismiss = onTutorialDismissed
            )
        }
    }
}

/**
 * PURPOSE:
 * One white pill button on the Main Menu.
 * Contains a circular icon on the left and a centered label.
 *
 * DESIGN:
 * - Button width: fillMaxWidth(0.6f)
 * - Button height: 48.dp
 * - Icon circle: 36.dp
 */
@Composable
private fun MenuPillButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .fillMaxWidth(0.6f)
            .height(48.dp)
            .clip(CircleShape)
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // ---- ICON CIRCLE ----
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(PrimarySlate),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
        }

        // ---- BUTTON LABEL ----
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = PrimarySlate
        )

        // ---- BALANCING SPACE ----
        Spacer(Modifier.size(36.dp))
    }
}
