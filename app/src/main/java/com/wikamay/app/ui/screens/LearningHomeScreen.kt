package com.wikamay.app.ui.screens //LEARNINGHOMESCREEN//

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wikamay.app.R
import com.wikamay.app.ui.components.LearningTopBar
import com.wikamay.app.ui.components.PrimaryPillButton
import com.wikamay.app.ui.components.TutorialOverlay
import com.wikamay.app.ui.components.TutorialStep
import com.wikamay.app.ui.theme.PrimarySlate
import com.wikamay.app.ui.theme.TextMuted

// ==========================================
// TEXTS
// EDIT HERE: Texts used on this screen.
// ==========================================
private const val MODULE_TITLE = "FSL Learning Module"
private const val MODULE_DESCRIPTION =
    "Practice the FSL alphabet and numbers, or follow short tutorial lessons to learn basic signs."

private const val FINGER_SPELLING_TITLE = "Finger Spelling"
private const val FINGER_SPELLING_DESCRIPTION = "Practice the FSL finger spelling"

private const val NUMBERS_TITLE = "Numbers"
private const val NUMBERS_SUBTITLE = "Practice the FSL number handshapes"

private const val PHRASES_CARD_TITLE = "FSL Words"
private const val PHRASES_CARD_SUBTITLE = "Learn everyday Words by category"

// Tutorial (guided walkthrough) texts. EDIT HERE.
private const val TUTORIAL_FINGER_SPELLING =
    "Finger Spelling: practice the FSL alphabet letter by letter."
private const val TUTORIAL_NUMBERS =
    "Numbers: practice the FSL number handshapes."
private const val TUTORIAL_PHRASES =
    "FSL Words: browse everyday phrases by category, such as Greetings and Survival."

// ==========================================
// COLORS (this screen only)
// EDIT HERE: Warm yellow/cream palette.
// ==========================================
internal val HeaderYellow = Color(0xFFFFC62E)
internal val CardCream = Color(0xFFFFF7E3)
internal val AccentOrange = Color(0xFFF5A00F)
private val DividerYellow = Color(0xFFFFE9A8)

// EDITABLE: Corner radius shared by cards and shadow depth.
internal val CardCorner = 20.dp
internal val CardShadow = 6.dp

/**
 * PURPOSE:
 * The LEARNING MENU. It opens from the upper-right button of both translation screens.
 *
 * Layout:
 * Header, Finger Spelling card (TRY), divider, Numbers card, FSL Phrases card.
 *
 * NAVIGATION:
 * - onBack: returns to the Translation screen.
 * - onOpenAlphabet: opens the letter selection screen.
 * - onOpenNumbers: opens the number selection screen.
 * - onOpenPhrases: opens the phrase category screen.
 * - onOpenTutorials: retained for compatibility.
 */
@Composable
fun LearningHomeScreen(
    onBack: () -> Unit,
    onOpenTutorials: () -> Unit,
    onOpenAlphabet: () -> Unit,
    onOpenNumbers: () -> Unit,
    onOpenPhrases: () -> Unit,
    showTutorial: Boolean = false,
    onTutorialFinished: () -> Unit = {},
    onTutorialDismissed: () -> Unit = onTutorialFinished
) {
    // ==========================================
    // TUTORIAL SUPPORT
    // ==========================================
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    var rootHeightPx by remember { mutableStateOf(0) }
    var fingerSpellingBounds by remember { mutableStateOf<Rect?>(null) }
    var numbersBounds by remember { mutableStateOf<Rect?>(null) }
    var phrasesBounds by remember { mutableStateOf<Rect?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { rootHeightPx = it.height }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
        ) {

            // ==========================================
            // HEADER
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(HeaderYellow, HeaderYellow, Color.White)
                        )
                    )
            ) {
                // HEADER BACKGROUND PNG
                Image(
                    painter = painterResource(
                        id = R.drawable.learninghomescreenheadr
                    ),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    // BACK BUTTON
                    LearningTopBar(onBack = onBack)

                    Spacer(Modifier.height(16.dp))

                    // TITLE
                    Text(
                        MODULE_TITLE,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 32.sp,
                            lineHeight = 38.sp
                        ),
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(0.62f)
                    )

                    Spacer(Modifier.height(8.dp))

                    // DESCRIPTION
                    Text(
                        MODULE_DESCRIPTION,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        modifier = Modifier.fillMaxWidth(0.62f)
                    )

                    Spacer(Modifier.height(24.dp))
                }
            }

            // ==========================================
            // BODY
            // ==========================================
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(Modifier.height(4.dp))

                // ==========================================
                // FINGER SPELLING CARD
                // ==========================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned {
                            fingerSpellingBounds = it.boundsInRoot()
                        }
                        .shadow(
                            CardShadow,
                            RoundedCornerShape(CardCorner)
                        )
                        .clip(RoundedCornerShape(CardCorner))
                        .background(CardCream)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            FINGER_SPELLING_TITLE,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AccentOrange
                        )

                        Text(
                            FINGER_SPELLING_DESCRIPTION,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // FINGER SPELLING IMAGE
                    Image(
                        painter = painterResource(
                            id = R.drawable.fsl_tutorial_preview
                        ),
                        contentDescription = "AR alphabet overlay preview",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(3f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                3.dp,
                                PrimarySlate,
                                RoundedCornerShape(16.dp)
                            ),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(Modifier.height(12.dp))

                    // TRY BUTTON
                    PrimaryPillButton(
                        text = "TRY",
                        onClick = onOpenAlphabet,
                        modifier = Modifier.width(140.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                // ==========================================
                // DIVIDER
                // ==========================================
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(2.dp)
                        .background(DividerYellow)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(20.dp))

                // ==========================================
                // NUMBERS CARD + FSL PHRASES CARD
                // ==========================================
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // NUMBERS CARD
                    LearnMenuCard(
                        title = NUMBERS_TITLE,
                        subtitle = NUMBERS_SUBTITLE,
                        onClick = onOpenNumbers,
                        modifier = Modifier.onGloballyPositioned {
                            numbersBounds = it.boundsInRoot()
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.FormatListNumbered,
                                contentDescription = "Numbers",
                                modifier = Modifier.fillMaxSize(),
                                tint = AccentOrange
                            )
                        }
                    )

                    // FSL PHRASES CARD
                    LearnMenuCard(
                        title = PHRASES_CARD_TITLE,
                        subtitle = PHRASES_CARD_SUBTITLE,
                        onClick = onOpenPhrases,
                        modifier = Modifier.onGloballyPositioned {
                            phrasesBounds = it.boundsInRoot()
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Chat,
                                contentDescription = "FSL Phrases",
                                modifier = Modifier.fillMaxSize(),
                                tint = AccentOrange
                            )
                        }
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
        }

        // ==========================================
        // TUTORIAL OVERLAY
        // ==========================================
        if (showTutorial) {
            val stepTargets = listOf(
                fingerSpellingBounds,
                numbersBounds,
                phrasesBounds
            )

            TutorialOverlay(
                steps = listOf(
                    TutorialStep(
                        fingerSpellingBounds,
                        TUTORIAL_FINGER_SPELLING
                    ),
                    TutorialStep(
                        numbersBounds,
                        TUTORIAL_NUMBERS
                    ),
                    TutorialStep(
                        phrasesBounds,
                        TUTORIAL_PHRASES
                    )
                ),
                onFinish = onTutorialFinished,
                onDismiss = onTutorialDismissed,

                // Scroll highlighted cards into view.
                onStepChange = { index ->
                    val bounds = stepTargets.getOrNull(index)

                    if (bounds != null && rootHeightPx > 0) {
                        val hidden = bounds.bottom - (rootHeightPx - 240f)

                        if (hidden > 0f) {
                            scope.launch {
                                scrollState.animateScrollTo(
                                    scrollState.value + hidden.toInt()
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}

/**
 * PURPOSE:
 * One horizontal rounded menu card:
 * icon area on the left, title and subtitle on the right.
 *
 * EDITABLE:
 * - Icon size: 56.dp
 * - Card color: CardCream
 * - Corner radius: CardCorner
 * - Shadow: CardShadow
 * - Title color: AccentOrange
 */
@Composable
private fun LearnMenuCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                CardShadow,
                RoundedCornerShape(CardCorner)
            )
            .clip(RoundedCornerShape(CardCorner))
            .background(CardCream)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ICON AREA
        Box(
            modifier = Modifier.size(56.dp),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        // TITLE + SUBTITLE
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = AccentOrange
            )

            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}