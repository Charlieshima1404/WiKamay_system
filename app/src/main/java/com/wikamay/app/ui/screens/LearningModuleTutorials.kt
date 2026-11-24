package com.wikamay.app.ui.screens //TUTORIAL OVERLAY LEARNING MODULE//

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import com.wikamay.app.ui.components.TutorialOverlay
import com.wikamay.app.ui.components.TutorialStep

// ============================================================================
// TUTORIAL NAVIGATION AND STEPS: LEARNING MODULE WALKTHROUGHS (THIS FILE)
// ----------------------------------------------------------------------------
// This file holds the guided walkthroughs that run INSIDE the Learning Module:
//   1. Alphabet / Finger Spelling: letter selection screen
//   2. Learning Phrases: category screen (FSL Phrases)
//   3. Learning Phrases: lesson list of a category
// Edit the tutorial TEXT and the ORDER of the steps here. Tooltip/highlight look: components/TutorialOverlay.kt.
// The screens only measure where their elements are; they do not change how any feature works.
// ============================================================================

// ===== TUTORIAL NAVIGATION AND STEPS: HIGHLIGHT NAMES (KEYS) =====
// Each highlighted element has a short name. A screen tags an element with
//   Modifier.tutorialTarget(targets, TutorialKeys.SOMETHING)
// and the walkthrough below looks the element up by the same name.
object TutorialKeys {
    const val ALPHABET_FIRST_TILE = "alphabet_first_tile"
    const val ALPHABET_DOTS = "alphabet_dots"
    const val ALPHABET_TOP_BAR = "alphabet_top_bar"
    const val PHRASE_FIRST_CATEGORY = "phrase_first_category"
    const val LESSON_FIRST_CARD = "lesson_first_card"
    const val LESSON_VIEW_BUTTON = "lesson_view_button"
}

/**
 * PURPOSE: Remembers where tagged elements are on screen. Used only to draw the tutorial highlight.
 * If an element is missing (not on screen), its step is simply skipped.
 */
class TutorialTargets {
    private val bounds = mutableStateMapOf<String, Rect>()
    fun boundsOf(key: String): Rect? = bounds[key]
    internal fun update(key: String, rect: Rect) {
        bounds[key] = rect
    }
}

@Composable
fun rememberTutorialTargets(): TutorialTargets = remember { TutorialTargets() }

/** PURPOSE: Tags a UI element so the tutorial can highlight it. Does not change how the element looks or works. */
fun Modifier.tutorialTarget(targets: TutorialTargets, key: String): Modifier =
    this.onGloballyPositioned { targets.update(key, it.boundsInRoot()) }

/** Builds the step list from (key, message) pairs, skipping elements that are not on screen. */
private fun buildSteps(targets: TutorialTargets, vararg stepDefinitions: Pair<String, String>): List<TutorialStep> =
    stepDefinitions.mapNotNull { (key, message) ->
        targets.boundsOf(key)?.let { TutorialStep(it, message) }
    }

// ===== TUTORIAL NAVIGATION AND STEPS: ALPHABET / FINGER SPELLING TEXT =====
private const val ALPHABET_TILE_TEXT =
    "Tap a letter to open its practice screen, where you can see the handshape and practice it with your camera."
private const val ALPHABET_DOTS_TEXT =
    "There are more letters! Swipe left or right, or tap a dot, to see the rest of the alphabet."
private const val ALPHABET_TOP_BAR_TEXT =
    "Use the arrow to go back to the Learning Module, or the translate button to return to the translator."

/**
 * PURPOSE: Walkthrough for the letter selection screen (Alphabet / Finger Spelling).
 *
 * ===== TUTORIAL NAVIGATION AND STEPS: ALPHABET STEPS =====
 *   1. A letter tile   2. The page dots   3. The top bar (Back and translate buttons)
 * To reorder, add or remove steps, edit the buildSteps(...) list below.
 */
@Composable
fun AlphabetSelectionTutorial(
    targets: TutorialTargets,
    onFinish: () -> Unit,
    onDismiss: () -> Unit
) {
    TutorialOverlay(
        steps = buildSteps(
            targets,
            TutorialKeys.ALPHABET_FIRST_TILE to ALPHABET_TILE_TEXT,
            TutorialKeys.ALPHABET_DOTS to ALPHABET_DOTS_TEXT,
            TutorialKeys.ALPHABET_TOP_BAR to ALPHABET_TOP_BAR_TEXT
        ),
        onFinish = onFinish,
        onDismiss = onDismiss
    )
}

// ===== TUTORIAL NAVIGATION AND STEPS: LEARNING PHRASES CATEGORY TEXT =====
private const val PHRASE_CATEGORY_TEXT =
    "Tap a category to see its lessons, for example Greetings. Scroll down to find more categories such as Food and Drink."

/**
 * PURPOSE: Walkthrough for the FSL Phrases category screen.
 *
 * ===== TUTORIAL NAVIGATION AND STEPS: PHRASE CATEGORY STEPS =====
 *   1. A category card (tapping a card opens that category's lesson list)
 */
@Composable
fun PhraseCategoriesTutorial(
    targets: TutorialTargets,
    onFinish: () -> Unit,
    onDismiss: () -> Unit
) {
    TutorialOverlay(
        steps = buildSteps(
            targets,
            TutorialKeys.PHRASE_FIRST_CATEGORY to PHRASE_CATEGORY_TEXT
        ),
        onFinish = onFinish,
        onDismiss = onDismiss
    )
}

// ===== TUTORIAL NAVIGATION AND STEPS: PHRASE LESSON LIST TEXT =====
private const val LESSON_CARD_TEXT =
    "Each card is one lesson. It shows the lesson number and the sign in English and Filipino. Scroll to see more lessons."
private const val LESSON_VIEW_BUTTON_TEXT =
    "Tap View Lesson to open the lesson, then use TRY inside it to practice the sign."

/**
 * PURPOSE: Walkthrough for the lesson list of a phrase category (Greetings, Survival, ...).
 *
 * ===== TUTORIAL NAVIGATION AND STEPS: PHRASE LESSON LIST STEPS =====
 *   1. A lesson card   2. Its View Lesson button
 */
@Composable
fun PhraseLessonsTutorial(
    targets: TutorialTargets,
    onFinish: () -> Unit,
    onDismiss: () -> Unit
) {
    TutorialOverlay(
        steps = buildSteps(
            targets,
            TutorialKeys.LESSON_FIRST_CARD to LESSON_CARD_TEXT,
            TutorialKeys.LESSON_VIEW_BUTTON to LESSON_VIEW_BUTTON_TEXT
        ),
        onFinish = onFinish,
        onDismiss = onDismiss
    )
}
