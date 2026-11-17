
package com.wikamay.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.paint
import com.wikamay.app.R
import com.wikamay.app.ui.components.PrimaryPillButton
import com.wikamay.app.ui.theme.PrimarySlate

// EDITABLE: Text shown on the Welcome screen.
private const val APP_TITLE = "WiKamay"
private const val INTRO_MESSAGE =
    "Translate and learn Filipino Sign Language (FSL) anywhere, with tools made for accessible and inclusive communication."
private const val BUTTON_LABEL = "Get Started"

/**
 * PURPOSE: The Welcome / Splash screen, the first screen shown when the app opens.
 * Layout: title, actual WiKamay logo, introductory message, and start button.
 * NAVIGATION: [onGetStarted] opens the main Translation screen.
 */
@Composable
fun SplashScreen(onGetStarted: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.wikamay_flashbackground),
                contentScale = ContentScale.Crop
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Spacer(Modifier.weight(1f))

        // FUNCTION: Opens the Translation screen.
        PrimaryPillButton(
            text = BUTTON_LABEL,
            onClick = onGetStarted,
            modifier = Modifier.fillMaxWidth(0.6f)
        )

        Spacer(Modifier.height(200.dp))
    }
}
