package com.wikamay.app.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wikamay.app.R
import com.wikamay.app.ui.components.LearningTopBar
import com.wikamay.app.ui.components.PrimaryPillButton
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
private const val FINGER_SPELLING_DESCRIPTION = "Start out with the basic alphabet!"
private const val NUMBERS_TITLE = "Numbers"

// ==========================================
// LEARNING CATEGORIES
// EDIT HERE: One card is created for every line below, in this order, under the Numbers card.
// - title: shown on the card AND used to find the lessons, so it must match the category name used
//   in SampleData.lessons (data/SampleData.kt).
// - subtitle: the small text under the title.
// - iconRes: PNG for the left icon area, e.g. iconRes = R.drawable.ic_greetings.
//   Leave it out (null) to keep the icon area empty until your PNG is ready.
// ==========================================
private data class LearnCategory(
    val title: String,
    @DrawableRes val iconRes: Int? = null
)

private val LEARN_CATEGORIES = listOf(
    LearnCategory("Greetings"),
    LearnCategory("Survival"),
    LearnCategory("Calendar"),
    LearnCategory("Days"),
    LearnCategory("Family"),
    LearnCategory("Relationships"),
    LearnCategory("Color"),
    LearnCategory("Food"),
    LearnCategory("Drink")
)

// ==========================================
// COLORS (this screen only)
// EDIT HERE: Warm yellow/cream palette from the reference design. Kept in this file so the
// app-wide colors in ui/theme/Color.kt are not changed.
// ==========================================
private val HeaderYellow = Color(0xFFFFC62E)   // top of the header gradient
private val CardCream = Color(0xFFFFF7E3)      // background of the Basic FSL / Letters / Numbers cards
private val AccentOrange = Color(0xFFF5A00F)   // "BASIC FSL", "Letters", "Numbers" headings
private val DividerYellow = Color(0xFFFFE9A8)  // thin divider under the Basic FSL card

// EDITABLE: Corner radius shared by the cards, and the shadow depth.
private val CardCorner = 20.dp
private val CardShadow = 6.dp

/**
 * PURPOSE: The LEARNING MENU. It opens from the upper-right button of both translation screens.
 * Layout (top to bottom): header, Finger Spelling card (TRY), divider, Numbers card, then one card
 * for each learning category (Greetings, Survival, Calendar, Days, Family, Relationships, Color,
 * Food, Drink).
 *
 * NAVIGATION:
 * - [onBack]: the top-left button returns to the Translation screen the user came from.
 * - [onOpenAlphabet]: the TRY button of the Finger Spelling card (opens the letter selection screen).
 * - [onOpenNumbers]: the Numbers card (unchanged).
 * - [onOpenCategory]: a category card; receives the category name (opens that category's lessons).
 * - [onOpenTutorials]: kept in the signature for compatibility. The full lesson list route still
 *   exists in the NavGraph, but no card on this screen opens it any more.
 */
@Composable
fun LearningHomeScreen(
    onBack: () -> Unit,
    onOpenTutorials: () -> Unit,
    onOpenAlphabet: () -> Unit,
    onOpenNumbers: () -> Unit,
    onOpenCategory: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // ==========================================
        // HEADER
        // EDIT HERE: Gradient colors -> HeaderYellow above. Height grows with the text.
        // The gradient runs edge to edge, behind the status bar.
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(HeaderYellow, HeaderYellow, Color.White)))
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
            )
            {
                // NAVIGATION: Back button -> onBack (unchanged).
                LearningTopBar(onBack = onBack)
                Spacer(Modifier.height(16.dp))

                // Text is limited to ~62% of the width so it never runs over the header image.
                Text(
                    MODULE_TITLE,
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp, lineHeight = 38.sp),
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth(0.62f)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    MODULE_DESCRIPTION,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    modifier = Modifier.fillMaxWidth(0.62f)
                )
                Spacer(Modifier.height(24.dp))
            }
        }

        // ---- Body (below the header) ----
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // ==========================================
            // FINGER SPELLING CARD (replaces the former BASIC FSL card)
            // EDIT HERE: Card color -> CardCream, corner -> CardCorner, heading color -> AccentOrange.
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(CardShadow, RoundedCornerShape(CardCorner))
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
                        fontSize = 22.sp, // slightly smaller than before so "Finger Spelling" fits beside its description
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

                // ------------------------------------------
                // FINGER SPELLING IMAGE (PNG)
                // EDIT HERE: This keeps the image your screen already used (R.drawable.fsl_alphabet_preview).
                // To use your new PNG, add it to res/drawable and change the drawable name below.
                // Shape/proportion: aspectRatio(3f) = 3 times wider than tall. Border: PrimarySlate, 3.dp.
                // ------------------------------------------
                Image(
                    painter = painterResource(id = R.drawable.fsl_tutorial_preview),
                    contentDescription = "AR alphabet overlay preview",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f)
                        .clip(RoundedCornerShape(16.dp))
                        .border(3.dp, PrimarySlate, RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(12.dp))

                // NAVIGATION: TRY -> onOpenAlphabet (opens the letter selection screen), unchanged.
                PrimaryPillButton(
                    text = "TRY",
                    onClick = onOpenAlphabet,
                    modifier = Modifier.width(140.dp)
                )
            }
            Spacer(Modifier.height(20.dp))

            // ==========================================
            // DIVIDER
            // EDIT HERE: Color -> DividerYellow. Thickness -> height. Width -> fillMaxWidth(fraction).
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
            // NUMBERS CARD + LEARNING CATEGORY CARDS
            // EDIT HERE: Text -> constants and LEARN_CATEGORIES at the top of this file.
            // Icons -> iconRes in LEARN_CATEGORIES (categories) / the icon slot below (Numbers).
            // ==========================================
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // NAVIGATION: Numbers card -> onOpenNumbers (opens the number selection screen). Unchanged.
                LearnMenuCard(
                    title = NUMBERS_TITLE,
                    onClick = onOpenNumbers,
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.FormatListNumbered,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            tint = AccentOrange
                        )
                    }
                )
                // NAVIGATION: Each category card -> onOpenCategory(category name), which opens the
                // lesson list of that category (same layout as the existing Tutorials list).
                LEARN_CATEGORIES.forEach { category ->
                    LearnMenuCard(
                        title = category.title,
                        onClick = { onOpenCategory(category.title) },
                        icon = {
                            // ---- CATEGORY ICON (PNG) ----
                            // EDIT HERE: Set iconRes for this category in LEARN_CATEGORIES.
                            // The area stays empty while iconRes is null.
                            category.iconRes?.let { iconRes ->
                                Image(
                                    painter = painterResource(id = iconRes),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

/**
 * PURPOSE: One horizontal rounded menu card: icon area on the left, title and subtitle on the right.
 * The whole card is tappable and calls [onClick].
 *
 * EDITABLE:
 * - Icon area size -> the `size(56.dp)` below. Fill it with a PNG through the [icon] slot.
 * - Card color/corner/shadow -> CardCream / CardCorner / CardShadow at the top of this file.
 * - Title color -> AccentOrange. Title/subtitle sizes -> the fontSize / style below.
 */
@Composable
private fun LearnMenuCard(
    title: String,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(CardShadow, RoundedCornerShape(CardCorner))
            .clip(RoundedCornerShape(CardCorner))
            .background(CardCream)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // PNG ICON AREA: stays an empty 56.dp space until an icon is supplied.
        Box(modifier = Modifier.size(56.dp), contentAlignment = Alignment.Center) {
            icon()
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = AccentOrange
            )
        }
    }
}
