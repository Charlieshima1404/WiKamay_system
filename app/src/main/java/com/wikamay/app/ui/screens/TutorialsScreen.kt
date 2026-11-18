package com.wikamay.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wikamay.app.data.Lesson
import com.wikamay.app.data.SampleData
import com.wikamay.app.ui.components.LearningTopBar
import com.wikamay.app.ui.components.PlaceholderBox
import com.wikamay.app.ui.components.PrimaryPillButton
import com.wikamay.app.ui.theme.PrimarySlate
import com.wikamay.app.ui.theme.SoftBlueGray
import com.wikamay.app.ui.theme.TextMuted

/**
 * PURPOSE: The Tutorials screen (wireframe: "Learn basic FSL" list of lesson cards).
 * Each card shows a video placeholder, the sign name, a short description and a View Lesson button.
 *
 * NAVIGATION:
 * - [onViewLesson]: opens the lesson details screen for the tapped lesson (receives the lesson id).
 * - [onBack]: one step back to the Learning Menu.
 * - [onReturnToTranslation]: jumps back to the Translation screen.
 *
 * CATEGORY: When [category] is given (Greetings, Survival, Food, ...), only the lessons of that category
 * are listed and the category name replaces the "Learn basic FSL" subtitle. When it is null the screen
 * behaves exactly as before and lists every lesson.
 */
@Composable
fun TutorialsScreen(
    onViewLesson: (Int) -> Unit,
    onBack: () -> Unit,
    onReturnToTranslation: () -> Unit,
    category: String? = null
) {
    // FUNCTION: Lessons shown in the list (all lessons, or only the chosen category).
    val lessons = if (category == null) SampleData.lessons else SampleData.lessons.filter { it.category == category }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        LearningTopBar(onBack = onBack, onReturnToTranslation = onReturnToTranslation)
        Spacer(Modifier.height(16.dp))

        Text("FSL Learning Module", style = MaterialTheme.typography.headlineMedium, color = PrimarySlate)
        Spacer(Modifier.height(8.dp))
        Text(category ?: "Learn basic FSL", style = MaterialTheme.typography.titleMedium, color = PrimarySlate)
        Spacer(Modifier.height(8.dp))

        // PURPOSE: Scrollable list. One LessonCard is created for every lesson in SampleData.lessons.
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(lessons) { index, lesson ->
                LessonCard(lesson = lesson, number = index + 1, onViewLesson = { onViewLesson(lesson.id) })
            }
        }
    }
}

/** PURPOSE: One lesson entry in the list. [number] is its position in the list (Lesson 1, 2, 3, ...). */
@Composable
private fun LessonCard(lesson: Lesson, number: Int, onViewLesson: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SoftBlueGray)
            .border(2.dp, PrimarySlate, shape)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // PLACEHOLDER: Replace with a sign image or video thumbnail.
        PlaceholderBox(
            modifier = Modifier.size(88.dp),
            icon = Icons.Filled.Videocam,
            showCross = false
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Lesson $number - ${lesson.category}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
            Text(lesson.signName, style = MaterialTheme.typography.titleMedium, color = PrimarySlate)
            Text(
                text = lesson.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(8.dp))
            // NAVIGATION: Opens the lesson details screen.
            PrimaryPillButton(text = "View Lesson", onClick = onViewLesson)
        }
    }
}
