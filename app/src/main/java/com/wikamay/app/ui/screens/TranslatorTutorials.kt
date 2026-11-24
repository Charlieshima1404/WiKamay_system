package com.wikamay.app.ui.screens //TUTORIAL OVERLAY TRANSLATION//

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Rect
import com.wikamay.app.ui.components.TutorialOverlay
import com.wikamay.app.ui.components.TutorialStep

// ==========================================
// TRANSLATOR TUTORIAL TEXTS
// EDIT HERE: Steps of the Text → Sign and Sign → Text guided walkthroughs.
// ==========================================
private const val TUTORIAL_AVATAR = "Avatar: shows the FSL sign translation of your text."
private const val TUTORIAL_SPEED = "Signing speed: adjust how fast or slow the avatar signs."
private const val TUTORIAL_INPUT = "Text input: type words or phrases here, or tap the mic to speak."
private const val TUTORIAL_LEARNING = "Learning Menu: access FSL alphabet, numbers, and phrase lessons."
private const val TUTORIAL_MODE_TOGGLE = "Translation mode: switch between Text → Sign and Sign → Text."

private const val TUTORIAL_SCAN_AREA = "Scan area: position your hands within the frame for sign recognition."
private const val TUTORIAL_SCAN_BUTTON = "Scan button: tap to capture and translate your sign."
private const val TUTORIAL_RESULT = "Translation result: view text translation and tap the speaker to hear it."
private const val TUTORIAL_CAMERA_SWITCH = "Camera switch: toggle between front and rear cameras."

/**
 * PURPOSE: Guided walkthrough for the Text → Sign screen.
 * Steps: Avatar area -> Speed slider -> Text input -> Learning shortcut -> Mode toggle.
 */
@Composable
fun TextToSignTutorial(
    avatarBounds: Rect?,
    speedBounds: Rect?,
    inputBounds: Rect?,
    learningShortcutBounds: Rect?,
    modeToggleBounds: Rect?,
    onFinish: () -> Unit,
    onDismiss: () -> Unit = onFinish,
) {
    TutorialOverlay(
        steps = listOf(
            TutorialStep(avatarBounds, TUTORIAL_AVATAR),
            TutorialStep(speedBounds, TUTORIAL_SPEED),
            TutorialStep(inputBounds, TUTORIAL_INPUT),
            TutorialStep(learningShortcutBounds, TUTORIAL_LEARNING),
            TutorialStep(modeToggleBounds, TUTORIAL_MODE_TOGGLE),
        ),
        onFinish = onFinish,
        onDismiss = onDismiss,
    )
}

/**
 * PURPOSE: Guided walkthrough for the Sign → Text screen.
 * Steps: Scan area -> Scan button -> Result panel -> Camera switch.
 */
@Composable
fun SignToTextTutorial(
    scanAreaBounds: Rect?,
    scanButtonBounds: Rect?,
    resultBounds: Rect?,
    cameraSwitchBounds: Rect?,
    onFinish: () -> Unit,
    onDismiss: () -> Unit = onFinish,
) {
    TutorialOverlay(
        steps = listOf(
            TutorialStep(scanAreaBounds, TUTORIAL_SCAN_AREA),
            TutorialStep(scanButtonBounds, TUTORIAL_SCAN_BUTTON),
            TutorialStep(resultBounds, TUTORIAL_RESULT),
            TutorialStep(cameraSwitchBounds, TUTORIAL_CAMERA_SWITCH),
        ),
        onFinish = onFinish,
        onDismiss = onDismiss,
    )
}
