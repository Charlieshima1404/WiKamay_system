package com.wikamay.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.wikamay.app.ui.theme.PrimarySlate
import com.wikamay.app.ui.theme.SoftBlueGray
import com.wikamay.app.ui.theme.TextMuted

/**
 * PURPOSE: A big tappable card used on the Learning Home screen ("Numbers", "Tutorials").
 * FUNCTION: Calls [onClick] when the card is tapped.
 */
@Composable
fun LearningCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp)
            .clip(shape)
            .background(SoftBlueGray)
            .border(2.dp, PrimarySlate, shape)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(icon, contentDescription = null, tint = PrimarySlate, modifier = Modifier.size(40.dp))
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium, color = PrimarySlate)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextMuted)
        }
    }
}
