package com.wikamay.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.wikamay.app.data.SampleData
import com.wikamay.app.ui.screens.ItemSelectionScreen
import com.wikamay.app.ui.screens.LearningHomeScreen
import com.wikamay.app.ui.screens.LessonDetailScreen
import com.wikamay.app.ui.screens.PracticeScreen
import com.wikamay.app.ui.screens.SignToTextScreen
import com.wikamay.app.ui.screens.SplashScreen
import com.wikamay.app.ui.screens.TextToSignScreen
import com.wikamay.app.ui.screens.TutorialsScreen
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition

/**
 * NAVIGATION: Connects all screens. Each screen receives simple "what to do when X is tapped"
 * functions (lambdas) from here, so the screens themselves never need to know about each other.
 *
 * Flow:  Splash -> Text to Sign  <->  Sign to Text
 *                       |                  |
 *                       +--- Learning Menu (Learning Home) ---+
 *                              |- Alphabet -> Practice
 *                              |- Numbers  -> Practice
 *                              |- Tutorials -> Lesson details
 *                              |- Category (Greetings, Survival, ...) -> Lesson details
 * There is no Home Screen: the Splash goes straight to Text to Sign.
 */
@Composable
fun WiKamayNavGraph() {
    // PURPOSE: The controller that performs navigation and remembers the back stack.
    val navController = rememberNavController()

    // NAVIGATION: Closes every learning screen and returns to whichever translation screen
    // the user came from (the translation screen is always below Learning Home in the back stack).
    val returnToTranslation: () -> Unit = {
        navController.popBackStack(Routes.LEARNING_HOME, inclusive = true)
    }

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {

        // ---- Splash ----
        composable(Routes.SPLASH) {
            SplashScreen(
                // NAVIGATION: Get Started -> main Translation screen. The splash is removed from the
                // back stack so pressing Back on Translation does not return to it.
                onGetStarted = {
                    navController.navigate(Routes.TEXT_TO_SIGN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // ---- Translation: Text/Speech to Sign (default screen) ----
        composable(Routes.TEXT_TO_SIGN) {
            TextToSignScreen(
                // NAVIGATION: Toggle -> reverse translation. The old translation screen is replaced,
                // so switching back and forth never piles up screens.
                onSwitchToSignToText = {
                    navController.navigate(Routes.SIGN_TO_TEXT) {
                        popUpTo(Routes.TEXT_TO_SIGN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onOpenLearningMenu = { navController.navigate(Routes.LEARNING_HOME) }
            )
        }

        // ---- Translation: Sign to Text/Speech ----
        composable(Routes.SIGN_TO_TEXT) {
            SignToTextScreen(
                onSwitchToTextToSign = {
                    navController.navigate(Routes.TEXT_TO_SIGN) {
                        popUpTo(Routes.SIGN_TO_TEXT) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onOpenLearningMenu = { navController.navigate(Routes.LEARNING_HOME) }
            )
        }

        // ---- Learning Menu ----
        composable(Routes.LEARNING_HOME) {
            LearningHomeScreen(
                onBack = { navController.popBackStack() },
                onOpenAlphabet = { navController.navigate(Routes.ALPHABET) },
                onOpenNumbers = { navController.navigate(Routes.NUMBERS) },
                onOpenTutorials = { navController.navigate(Routes.TUTORIALS) },
                // NAVIGATION: Category card (Greetings, Survival, ...) -> that category's lesson list.
                onOpenCategory = { category -> navController.navigate(Routes.category(category)) }
            )
        }

        // ---- Alphabet: choose a letter ----
        composable(Routes.ALPHABET) {
            ItemSelectionScreen(
                title = "Choose what letter to learn",
                items = SampleData.alphabet,
                onItemSelected = { index ->
                    navController.navigate(Routes.practice(Routes.TYPE_ALPHABET, index))
                },
                onBack = { navController.popBackStack() },
                onReturnToTranslation = returnToTranslation
            )
        }

        // ---- Numbers: choose a number ----
        composable(Routes.NUMBERS) {
            ItemSelectionScreen(
                title = "Choose what number to learn",
                items = SampleData.numbers,
                onItemSelected = { index ->
                    navController.navigate(Routes.practice(Routes.TYPE_NUMBERS, index))
                },
                onBack = { navController.popBackStack() },
                onReturnToTranslation = returnToTranslation
            )
        }

        // ---- Practice screen (shared by Alphabet and Numbers) ----
        composable(
            route = Routes.PRACTICE,
            arguments = listOf(
                navArgument("type") { type = NavType.StringType },
                navArgument("index") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: Routes.TYPE_ALPHABET
            val startIndex = backStackEntry.arguments?.getInt("index") ?: 0
            val isAlphabet = type == Routes.TYPE_ALPHABET
            PracticeScreen(
                screenTitle = if (isAlphabet) "FSL Alphabet" else "FSL Numbers",
                itemKind = if (isAlphabet) "Letter" else "Number",
                items = if (isAlphabet) SampleData.alphabet else SampleData.numbers,
                startIndex = startIndex,
                onBack = { navController.popBackStack() },
                onReturnToTranslation = returnToTranslation
            )
        }

        // ---- Tutorials list ----
        composable(Routes.TUTORIALS) {
            TutorialsScreen(
                onViewLesson = { lessonId -> navController.navigate(Routes.lesson(lessonId)) },
                onBack = { navController.popBackStack() },
                onReturnToTranslation = returnToTranslation
            )
        }

        // ---- Category lesson list (reuses the Tutorials screen, filtered by category) ----
        composable(
            route = Routes.CATEGORY,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            TutorialsScreen(
                onViewLesson = { lessonId -> navController.navigate(Routes.lesson(lessonId)) },
                onBack = { navController.popBackStack() },
                onReturnToTranslation = returnToTranslation,
                category = backStackEntry.arguments?.getString("category")
            )
        }

        // ---- Lesson details ----
        composable(
            route = Routes.LESSON,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            LessonDetailScreen(
                initialLessonId = backStackEntry.arguments?.getInt("id") ?: 1,
                onBack = { navController.popBackStack() },
                onReturnToTranslation = returnToTranslation
            )
        }
    }
}
