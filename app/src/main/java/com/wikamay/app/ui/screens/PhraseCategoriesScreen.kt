package com.wikamay.app.ui.screens //INSIDE THE FSL WORDS on the homepage yung maraming box toh//

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wikamay.app.R
import com.wikamay.app.ui.components.LearningTopBar

// ===== LEARNING PHRASES CATEGORY UI: EDIT SCREEN TEXTS HERE =====
private const val PHRASES_TITLE = "FSL Words"
private const val CATEGORY_HEADING = "Category"

// ===== CATEGORY MODEL =====
private data class LearnCategory(
    val title: String,
    val icon: ImageVector
)

// ===== CATEGORY TITLES AND ICONS =====
private val LEARN_CATEGORIES = listOf(
    LearnCategory("Greetings", Icons.Filled.WavingHand),
    LearnCategory("Survival", Icons.Filled.HealthAndSafety),
    LearnCategory("Calendar", Icons.Filled.CalendarMonth),
    LearnCategory("Days", Icons.Filled.CalendarMonth),
    LearnCategory("Family", Icons.Filled.FamilyRestroom),
    LearnCategory("Relationships", Icons.Filled.Favorite),
    LearnCategory("Color", Icons.Filled.ColorLens),
    LearnCategory("Food", Icons.Filled.Restaurant),
    LearnCategory("Drink", Icons.Filled.LocalDrink)
)

/*
 * PURPOSE:
 * Displays the FSL Phrases screen with a two-column category grid.
 *
 * NAVIGATION:
 * - onBack: returns to the Learning Menu.
 * - onOpenCategory: opens the selected category's lesson list.
 */

@Composable
fun PhraseCategoriesScreen(
    onBack: () -> Unit,
    onOpenCategory: (String) -> Unit,
    showTutorial: Boolean = false,
    onTutorialFinished: () -> Unit = {},
    onTutorialDismissed: () -> Unit = onTutorialFinished
) {

    // Tutorial highlight target.
    val tutorialTargets = rememberTutorialTargets()

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {

            // ===== HEADER =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                HeaderYellow,
                                HeaderYellow,
                                Color.White
                            )
                        )
                    )
            ) {

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
                        .padding(
                            horizontal = 20.dp,
                            vertical = 12.dp
                        )
                ) {

                    // Back button.
                    LearningTopBar(onBack = onBack)

                    Spacer(Modifier.height(16.dp))

                    // Screen title.
                    Text(
                        PHRASES_TITLE,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 32.sp,
                            lineHeight = 38.sp
                        ),
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(0.62f)
                    )

                    Spacer(Modifier.height(24.dp))
                }
            }

            // ===== CATEGORY GRID =====
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp)
            ) {

                Text(
                    CATEGORY_HEADING,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AccentOrange
                )

                Spacer(Modifier.height(12.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // Two cards per row.
                    LEARN_CATEGORIES.chunked(2).forEach { rowItems ->

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            rowItems.forEach { category ->

                                CategoryCard(
                                    title = category.title,
                                    icon = category.icon,
                                    onClick = {
                                        onOpenCategory(category.title)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .then(
                                            if (category == LEARN_CATEGORIES.first()) {
                                                Modifier.tutorialTarget(
                                                    tutorialTargets,
                                                    TutorialKeys.PHRASE_FIRST_CATEGORY
                                                )
                                            } else {
                                                Modifier
                                            }
                                        )
                                )
                            }

                            // Keep the last card aligned to the same width.
                            if (rowItems.size == 1) {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }

        // ===== TUTORIAL OVERLAY =====
        if (showTutorial) {
            PhraseCategoriesTutorial(
                targets = tutorialTargets,
                onFinish = onTutorialFinished,
                onDismiss = onTutorialDismissed
            )
        }
    }
}

// ===== CATEGORY CARD DESIGN =====

@Composable
private fun CategoryCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .heightIn(min = 150.dp)
            .shadow(CardShadow, RoundedCornerShape(CardCorner))
            .clip(RoundedCornerShape(CardCorner))
            .background(CardCream)
            .clickable(onClick = onClick)
    ) {

        // CATEGORY TITLE (top-left).
        Column(
            modifier = Modifier.padding(
                start = 16.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = 56.dp
            )
        ) {

            Text(
                title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AccentOrange
            )
        }

        // CATEGORY ICON (bottom-right).
        Icon(
            imageVector = icon,
            contentDescription = "$title icon",
            tint = AccentOrange,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .size(44.dp)
        )
    }
}