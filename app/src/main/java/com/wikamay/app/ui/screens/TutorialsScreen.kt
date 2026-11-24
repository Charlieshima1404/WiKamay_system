package com.wikamay.app.ui.screens //LESSON SCREEN//

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
 * PURPOSE:
 * The Tutorials screen displaying a list of FSL lessons.
 * LESSON SCREEN
 *
 * Each card shows:
 * - A video placeholder
 * - Lesson number and sign name
 * - A View Lesson button
 *
 * NAVIGATION:
 * - onViewLesson: Opens the lesson details screen for the selected lesson.
 * - onBack: Returns to the Learning Menu.
 * - onReturnToTranslation: Navigates back to the Translation screen.
 *
 * CATEGORY:
 * - When category is provided, only lessons belonging to that category are shown.
 * - When category is null, all lessons are displayed.
 */
@Composable
fun TutorialsScreen(
    onViewLesson: (Int) -> Unit,
    onBack: () -> Unit,
    onReturnToTranslation: () -> Unit,
    category: String? = null,

    // Tutorial navigation and walkthrough inputs
    showTutorial: Boolean = false,
    onTutorialFinished: () -> Unit = {},
    onTutorialDismissed: () -> Unit = onTutorialFinished
) {

    // Remembers tutorial highlight targets.
    val tutorialTargets = rememberTutorialTargets()

    // Filter lessons based on the selected category.
    val lessons = if (category == null) {
        SampleData.lessons
    } else {
        SampleData.lessons.filter { it.category == category }
    }

    // Main screen container for the tutorial overlay.
    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .systemBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {

            // Top navigation bar.
            LearningTopBar(
                onBack = onBack,
                onReturnToTranslation = onReturnToTranslation
            )

            Spacer(Modifier.height(16.dp))

            // Screen title.
            Text(
                text = "FSL Learning Module",
                style = MaterialTheme.typography.headlineMedium,
                color = PrimarySlate
            )

            Spacer(Modifier.height(8.dp))

            // Subtitle: selected category or default title.
            Text(
                text = category ?: "Learn basic FSL",
                style = MaterialTheme.typography.titleMedium,
                color = PrimarySlate
            )

            Spacer(Modifier.height(8.dp))

            // Scrollable list of lessons.
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                itemsIndexed(lessons) { index, lesson ->

                    LessonCard(
                        lesson = lesson,
                        number = index + 1,
                        onViewLesson = {
                            onViewLesson(lesson.id)
                        },

                        // Tutorial highlight targets.
                        cardModifier = if (index == 0) {
                            Modifier.tutorialTarget(
                                tutorialTargets,
                                TutorialKeys.LESSON_FIRST_CARD
                            )
                        } else {
                            Modifier
                        },

                        buttonModifier = if (index == 0) {
                            Modifier.tutorialTarget(
                                tutorialTargets,
                                TutorialKeys.LESSON_VIEW_BUTTON
                            )
                        } else {
                            Modifier
                        }
                    )
                }
            }
        }

        // Tutorial walkthrough overlay.
        if (showTutorial) {
            PhraseLessonsTutorial(
                targets = tutorialTargets,
                onFinish = onTutorialFinished,
                onDismiss = onTutorialDismissed
            )
        }
    }
}

/**
 * PURPOSE:
 * Displays one lesson entry in the list.
 *
 * The lesson number is based on its position in the list.
 * The lesson title displays the actual sign name.
 */
@Composable
private fun LessonCard(
    lesson: Lesson,
    number: Int,
    onViewLesson: () -> Unit,
    cardModifier: Modifier = Modifier,
    buttonModifier: Modifier = Modifier
) {

    val shape = RoundedCornerShape(12.dp)

    Row(
        modifier = cardModifier
            .fillMaxWidth()
            .clip(shape)
            .background(SoftBlueGray)
            .border(
                2.dp,
                PrimarySlate,
                shape
            )
            .padding(12.dp),

        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {

        // Video placeholder.
        PlaceholderBox(
            modifier = Modifier.size(88.dp),
            icon = Icons.Filled.Videocam,
            showCross = false
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            // Lesson number and actual sign name.
            Text(
                text = "Lesson $number - ${lesson.signName}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(8.dp))

            // Opens the lesson details screen.
            PrimaryPillButton(
                text = "View Lesson",
                onClick = onViewLesson,
                modifier = buttonModifier
            )
        }
    }
}