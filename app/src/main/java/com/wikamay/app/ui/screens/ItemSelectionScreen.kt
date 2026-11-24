package com.wikamay.app.ui.screens //SELECTION THE LETTERS AND NUMBERS//

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wikamay.app.ui.components.LearningTopBar
import com.wikamay.app.ui.theme.PrimarySlate
import com.wikamay.app.ui.theme.TextMuted
import kotlinx.coroutines.launch

// EDITABLE: How many tiles appear per page (2 columns x 3 rows, as in the wireframe).
private const val COLUMNS = 2
private const val ROWS = 3
private const val ITEMS_PER_PAGE = COLUMNS * ROWS

/**
 * PURPOSE: Selection grid shared by the FSL Alphabet and Numbers sections
 * (wireframe: "Choose what letter to learn", tiles in a 2x3 grid, page dots below).
 * FUNCTION: Swipe left/right (or tap a dot) to change pages. Tapping a tile selects that item.
 *
 * NAVIGATION:
 * - [onItemSelected]: opens the practice screen for the tapped item (receives its position).
 * - [onBack]: one step back to the Learning Menu.
 * - [onReturnToTranslation]: jumps back to the Translation screen.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemSelectionScreen(
    title: String,
    items: List<String>,
    onItemSelected: (Int) -> Unit,
    onBack: () -> Unit,
    onReturnToTranslation: () -> Unit,
    // ===== TUTORIAL NAVIGATION AND STEPS: TUTORIAL INPUTS =====
    // Only used to show the walkthrough on top of this screen. Default = no tutorial.
    showTutorial: Boolean = false,
    onTutorialFinished: () -> Unit = {},
    onTutorialDismissed: () -> Unit = onTutorialFinished
) {
    // Remembers where the letter tile, dots and top bar are (only used by the tutorial highlight).
    val tutorialTargets = rememberTutorialTargets()

    // PURPOSE: Number of pages needed to show all items (rounded up).
    val pageCount = (items.size + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE
    val pagerState = rememberPagerState(pageCount = { pageCount })
    val scope = rememberCoroutineScope()

    // Box added only so the tutorial overlay can be drawn on top of the screen.
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .systemBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            LearningTopBar(
                onBack = onBack,
                onReturnToTranslation = onReturnToTranslation,
                // Tutorial: highlight target (Back and translate buttons)
                modifier = Modifier.tutorialTarget(tutorialTargets, TutorialKeys.ALPHABET_TOP_BAR)
            )
            Spacer(Modifier.height(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = PrimarySlate,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Swipe or tap the dots for more",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // ---- Pages of tiles ----
            HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
                val firstIndexOnPage = page * ITEMS_PER_PAGE
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    for (row in 0 until ROWS) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            for (column in 0 until COLUMNS) {
                                val index = firstIndexOnPage + row * COLUMNS + column
                                if (index < items.size) {
                                    SelectionTile(
                                        label = items[index],
                                        onClick = { onItemSelected(index) },
                                        // Tutorial: highlight target = the first tile
                                        modifier = if (index == 0) {
                                            Modifier.tutorialTarget(tutorialTargets, TutorialKeys.ALPHABET_FIRST_TILE)
                                        } else {
                                            Modifier
                                        }
                                    )
                                } else {
                                    // Keeps the grid aligned when the last page has fewer items.
                                    Spacer(Modifier.size(TILE_SIZE))
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }

            // ---- Page dots ----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .tutorialTarget(tutorialTargets, TutorialKeys.ALPHABET_DOTS), // Tutorial: highlight target
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pageCount) { pageNumber ->
                    val isCurrent = pageNumber == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .padding(6.dp)
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(if (isCurrent) PrimarySlate else Color.White)
                            .border(2.dp, PrimarySlate, CircleShape)
                            // FUNCTION: Tapping a dot scrolls to that page.
                            .clickable { scope.launch { pagerState.animateScrollToPage(pageNumber) } }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        // ===== TUTORIAL NAVIGATION AND STEPS: ALPHABET WALKTHROUGH =====
        // Drawn on top of the screen only while showTutorial is true (the NavGraph turns it on for the
        // FSL Alphabet only, not for Numbers). Text and steps: ui/screens/LearningModuleTutorials.kt.
        if (showTutorial) {
            AlphabetSelectionTutorial(
                targets = tutorialTargets,
                onFinish = onTutorialFinished,
                onDismiss = onTutorialDismissed
            )
        }
    } // end of the Box added for the tutorial overlay
}

// EDITABLE: Size of each letter/number tile.
private val TILE_SIZE = 110.dp

/** PURPOSE: One dark rounded tile showing a letter or number. Tapping it selects the item. */
@Composable
private fun SelectionTile(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(TILE_SIZE)
            .clip(RoundedCornerShape(16.dp))
            .background(PrimarySlate)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
