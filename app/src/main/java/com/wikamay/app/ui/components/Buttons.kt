package com.wikamay.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wikamay.app.ui.theme.PanelGray
import com.wikamay.app.ui.theme.PrimarySlate
import com.wikamay.app.ui.theme.SoftBlueGray

/**
 * PURPOSE: The main dark pill-shaped button (e.g. "Get Started", "TRY", "View Lesson").
 * FUNCTION: Calls [onClick] when tapped. Set [enabled] = false to gray it out.
 */
@Composable
fun PrimaryPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = PrimarySlate, contentColor = Color.White)
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, fontWeight = FontWeight.Bold)
    }
}

/**
 * PURPOSE: The lighter pill button used for secondary actions (e.g. "Previous lesson").
 */
@Composable
fun SecondaryPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = SoftBlueGray, contentColor = PrimarySlate)
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

/**
 * PURPOSE: The round dark icon button from the wireframe (microphone, scan, speaker, previous/next).
 * EDITABLE: Change [size] to make the button bigger or smaller.
 */
@Composable
fun RoundActionButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    enabled: Boolean = true
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier.size(size),
        enabled = enabled,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = PrimarySlate,
            contentColor = Color.White
        )
    ) {
        Icon(icon, contentDescription = contentDescription)
    }
}

/**
 * PURPOSE: The small gray square icon button in the screen corners of the wireframe
 * (Learning Menu button on translation screens, Back button on learning screens).
 */
@Composable
fun SquareIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(PanelGray)
    ) {
        IconButton(onClick = onClick, modifier = Modifier.fillMaxSize()) {
            Icon(icon, contentDescription = contentDescription, tint = PrimarySlate)
        }
    }
}
