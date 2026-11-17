package com.wikamay.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.ColorFilter
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
import androidx.compose.material.icons.filled.School
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
private const val BASIC_FSL_TITLE = "BASIC FSL"
private const val BASIC_FSL_DESCRIPTION = "Learn the FSL the easy way, one sign, one step at a time!"
private const val LETTERS_TITLE = "Letters"
private const val LETTERS_SUBTITLE = "Practice the FSL finger spelling"
private const val NUMBERS_TITLE = "Numbers"
private const val NUMBERS_SUBTITLE = "Practice the FSL number handshapes"
private const val TUTORIALS_TITLE = "Tutorials"
private const val TUTORIALS_SUBTITLE = "Short lessons for basic FSL signs"

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
 * Layout (top to bottom): header, BASIC FSL card (TRY), divider, Letters card, Numbers card,
 * and the Tutorials card (kept so the Tutorials section stays reachable).
 *
 * NAVIGATION (unchanged):
 * - [onBack]: the top-left button returns to the Translation screen the user came from.
 * - [onOpenAlphabet]: TRY button AND the Letters card.
 * - [onOpenNumbers]: the Numbers card.
 * - [onOpenTutorials]: the Tutorials card.
 */
@Composable
fun LearningHomeScreen(
    onBack: () -> Unit,
    onOpenTutorials: () -> Unit,
    onOpenAlphabet: () -> Unit,
    onOpenNumbers: () -> Unit
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
            // BASIC FSL CARD
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
                        BASIC_FSL_TITLE,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AccentOrange
                    )
                    Text(
                        BASIC_FSL_DESCRIPTION,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(12.dp))

                // ------------------------------------------
                // BASIC FSL IMAGE (PNG)
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
                    onClick = onOpenTutorials,
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
            // LETTERS / NUMBERS / TUTORIALS CARDS
            // EDIT HERE: Text -> constants at the top of this file. Icons -> see LearnMenuCard below.
            // ==========================================
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // NAVIGATION: Letters card -> onOpenAlphabet (opens the letter selection screen).
                LearnMenuCard(
                    title = LETTERS_TITLE,
                    subtitle = LETTERS_SUBTITLE,
                    onClick = onOpenAlphabet,
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.from_a_to_z_svgrepo_com),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            colorFilter = ColorFilter.tint(Color(0xFFF5A00F))
                        )
                        // ---- LETTERS ICON (PNG PLACEHOLDER) ----
                        // EDIT HERE: Replace this comment with:
                        // Image(painter = painterResource(id = R.drawable.YOUR_LETTERS_ICON),
                        //       contentDescription = null, modifier = Modifier.fillMaxSize())
                    }
                )
                // NAVIGATION: Numbers card -> onOpenNumbers (opens the number selection screen).
                LearnMenuCard(
                    title = NUMBERS_TITLE,
                    subtitle = NUMBERS_SUBTITLE,
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
                // NAVIGATION: Tutorials card -> onOpenTutorials (opens the tutorial list).
                // KEPT from the previous screen (not in the reference image) so Tutorials stays reachable.
                // It keeps its original icon. To hide it, delete this block (the callback stays in the signature).
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
    subtitle: String,
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
        Box(modifier = Modifier.size(56.dp), contentAlignment = Alignment.Center) { icon() }

        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AccentOrange)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
