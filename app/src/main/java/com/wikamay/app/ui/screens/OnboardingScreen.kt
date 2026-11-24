package com.wikamay.app.ui.screens //3 screen toh for the first  time user only it will appear//

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// ===== ONBOARDING STYLE: BACKGROUND GRADIENT (also used by the Main Menu) =====
// The blue -> yellow-green -> mint gradient behind the onboarding slides AND the Main Menu screen.
// Edit the three colors below (top, middle, bottom) to change both screens at once.
internal val WelcomeGradient: Brush = Brush.verticalGradient(
    listOf(Color(0xFF2B7DB8), Color(0xFFCFD76B), Color(0xFF6FE5B8))
)

// ===== ONBOARDING UI: EDIT SLIDE CONTENT HERE =====
// One OnboardingPage(...) per slide, shown in this order.
//  - intro / introBold : optional small line above the title (the bold part is the app name).
//  - title             : the big heading.
//  - description       : the paragraph under the heading.
//  - illustrationRes   : optional PNG shown faintly at the bottom of the slide
//                        (put the PNG in res/drawable and write R.drawable.your_png_name).
//                        Leave it as null for no illustration.
// To add a slide, copy one OnboardingPage(...) block. The page dots update automatically.
private data class OnboardingPage(
    val intro: String?,
    val introBold: String?,
    val title: String,
    val description: String,
    @DrawableRes val illustrationRes: Int? = null
)

private val ONBOARDING_PAGES = listOf(
    OnboardingPage(
        intro = "Welcome to ",
        introBold = "WiKamay",
        title = "Speak or Type, See It Sign",
        description = "Turn your spoken words or typed messages into Filipino Sign Language through a virtual signing avatar."
    ),
    OnboardingPage(
        intro = null,
        introBold = null,
        title = "Sign, and Let Your Hands Speak",
        description = "Use your camera to recognize selected Filipino Sign Language signs and translate them into text and speech."
    ),
    OnboardingPage(
        intro = null,
        introBold = null,
        title = "Learn Signs, One Step at a Time",
        description = "Explore visual tutorials and practice basic Filipino Sign Language through interactive guidance."
    )
)

// ===== ONBOARDING UI: EDIT BUTTON LABELS HERE =====
private const val SKIP_LABEL = "Skip"                       // top-right button
private const val NEXT_LABEL = "Next >"                     // bottom button on slides 1 and 2
private const val START_TUTORIAL_LABEL = "start tutorial"   // bottom button on the last slide

// ===== ONBOARDING ANIMATION: PAGE CHANGE SPEED =====
// How long (in milliseconds) the slide takes to move when the user taps "Next >".
// Bigger number = slower. Swiping with a finger uses Android's built-in swipe animation.
private const val ONBOARDING_PAGE_ANIMATION_MS = 400

/**
 * PURPOSE: The first-launch ONBOARDING: three swipeable intro pages shown once, before the Main Menu.
 *
 * NAVIGATION (provided by WiKamayNavGraph):
 * - [onSkip]: the Skip button (top-right). Goes to the Main Menu without the guided tutorial.
 * - [onStartTutorial]: the button on the last page. Goes to the Main Menu and starts the guided tutorial.
 * "Next >" only moves to the next page; it does not leave the screen.
 *
 * Whether this screen is shown at all is decided in navigation/WiKamayNavGraph.kt
 * (search for "FIRST-LAUNCH UI LOGIC").
 */
@Composable
fun OnboardingScreen(
    onSkip: () -> Unit,
    onStartTutorial: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { ONBOARDING_PAGES.size })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == ONBOARDING_PAGES.lastIndex

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WelcomeGradient) // ONBOARDING STYLE: gradient defined at the top of this file
    ) {
        // ===== ONBOARDING UI: EDIT SLIDE LAYOUT HERE =====
        // How each slide is arranged: the text block is vertically centered with 24.dp side padding.
        //  - Left/right margin ........ padding(horizontal = 24.dp)
        //  - Vertical position ........ verticalArrangement (Center). Use Arrangement.Top / Bottom to move it.
        //  - Illustration position .... the Image below (bottom center, faint).
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { pageIndex ->
            val page = ONBOARDING_PAGES[pageIndex]
            Box(modifier = Modifier.fillMaxSize()) {
                // Optional illustration (only drawn when illustrationRes is set).
                if (page.illustrationRes != null) {
                    Image(
                        painter = painterResource(id = page.illustrationRes),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 140.dp)
                            .size(180.dp)
                            .alpha(0.3f), // ONBOARDING STYLE: how faint the illustration is (0 = invisible, 1 = solid)
                        contentScale = ContentScale.Fit
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    // ===== ONBOARDING STYLE: FONTS AND SPACING =====
                    // Text sizes/weights/colors for the slide texts, and the gaps (Spacer heights) between them.
                    if (page.intro != null) {
                        Text(
                            text = buildAnnotatedString {
                                append(page.intro)
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(page.introBold.orEmpty()) }
                            },
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Spacer(Modifier.height(4.dp))
                    }
                    Text(
                        page.title,
                        fontSize = 26.sp,
                        lineHeight = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(page.description, fontSize = 15.sp, color = Color.White)
                }
            }
        }

        // ===== ONBOARDING UI: EDIT SKIP BUTTON HERE =====
        // Position: top-right corner (align TopEnd + 16.dp padding). Label: SKIP_LABEL above.
        // NAVIGATION: tapping it calls onSkip (see NavGraph: marks onboarding as done, opens the Main Menu).
        OnboardingOutlinedButton(
            text = SKIP_LABEL,
            onClick = onSkip,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(16.dp)
        )

        // ===== ONBOARDING UI: EDIT NEXT / START-TUTORIAL BUTTON AND PAGE DOTS HERE =====
        // Bottom area: the button (width 150.dp) and the page dots under it. Distance from the bottom edge:
        // padding(bottom = 40.dp).
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OnboardingOutlinedButton(
                text = if (isLastPage) START_TUTORIAL_LABEL else NEXT_LABEL,
                onClick = {
                    if (isLastPage) {
                        onStartTutorial() // NAVIGATION: last slide -> Main Menu + guided tutorial
                    } else {
                        // ONBOARDING ANIMATION: slides to the next page. Speed: ONBOARDING_PAGE_ANIMATION_MS.
                        scope.launch {
                            pagerState.animateScrollToPage(
                                page = pagerState.currentPage + 1,
                                animationSpec = tween(durationMillis = ONBOARDING_PAGE_ANIMATION_MS)
                            )
                        }
                    }
                },
                modifier = Modifier.width(150.dp)
            )
            Spacer(Modifier.height(16.dp))

            // ===== ONBOARDING STYLE: PAGE DOTS =====
            // The current page is a longer dark pill (24.dp wide); other pages are small light dots (8.dp).
            // Edit the colors/sizes below. The dots do not animate; they change instantly with the page.
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                repeat(ONBOARDING_PAGES.size) { index ->
                    val selected = index == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(if (selected) 24.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (selected) Color(0xFF3F4756) else Color.White.copy(alpha = 0.4f))
                    )
                }
            }
        }
    }
}

// ===== ONBOARDING STYLE: OUTLINED BUTTON LOOK (Skip / Next / start tutorial) =====
// Thin white outline, white text, fully rounded (CircleShape), 36.dp tall, 14.sp text.
// Edit border color/thickness, text color (contentColor), height, shape and font size here.
@Composable
private fun OnboardingOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        shape = CircleShape,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.8f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
    ) {
        Text(text, fontSize = 14.sp)
    }
}
