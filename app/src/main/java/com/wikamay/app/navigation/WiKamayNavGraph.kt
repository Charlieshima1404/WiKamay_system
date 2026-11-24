package com.wikamay.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.wikamay.app.data.OnboardingPrefs
import com.wikamay.app.ui.screens.MainMenuScreen
import com.wikamay.app.ui.screens.OnboardingScreen
import com.wikamay.app.ui.screens.PhraseCategoriesScreen
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

/**
 * NAVIGATION: Connects all screens. Each screen receives simple "what to do when X is tapped"
 * functions (lambdas) from here, so the screens themselves never need to know about each other.
 *
 * Flow:  Splash -> (first launch only) Onboarding -> Main Menu
 *        Main Menu -> Translator: Text to Sign  <->  Sign to Text
 *        Main Menu -> Learning Module: Learning Menu (Learning Home)
 *        (the Learning Menu can also be opened from both translation screens)
 *                              |- Alphabet -> Practice
 *                              |- Numbers  -> Practice
 *                              |- FSL Phrases -> Category (Greetings, Survival, ...) -> Lesson details
 *                              |- Tutorials (full lesson list; route kept, no card opens it)
 */
@Composable
fun WiKamayNavGraph() {
    // PURPOSE: The controller that performs navigation and remembers the back stack.
    val navController = rememberNavController()

    // ===== FIRST-LAUNCH UI LOGIC: SAVED FLAGS =====
    // Reads/writes the small true/false flags that remember (even after the app is closed) whether the
    // onboarding was already shown and which guided tutorials are still waiting. See data/OnboardingPrefs.kt.
    val onboardingPrefs = OnboardingPrefs(LocalContext.current)

    // NAVIGATION: Closes every learning screen and returns to whichever translation screen
    // the user came from (the translation screen is always below Learning Home in the back stack).
    // When the Learning Module was opened from the Main Menu there is no translation screen below it,
    // so the Translator (Text to Sign) is opened instead.
    val returnToTranslation: () -> Unit = {
        navController.popBackStack(Routes.LEARNING_HOME, inclusive = true)
        if (navController.currentDestination?.route == Routes.MAIN_MENU) {
            navController.navigate(Routes.TEXT_TO_SIGN)
        }
    }

    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        // ---- Splash ----
        composable(Routes.SPLASH) {
            SplashScreen(
                // ===== FIRST-LAUNCH UI LOGIC: SHOW ONBOARDING ONLY ON FIRST LAUNCH =====
                // Get Started -> Onboarding when "onboarding done" is false (first launch), otherwise the Main Menu.
                // To ALWAYS show the onboarding (for testing), replace the condition with `false`.
                // The splash is removed from the back stack so pressing Back does not return to it.
                onGetStarted = {
                    val next = if (onboardingPrefs.isOnboardingDone()) Routes.MAIN_MENU else Routes.ONBOARDING
                    navController.navigate(next) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // ===== FIRST-LAUNCH UI LOGIC: ONBOARDING COMPLETED OR SKIPPED =====
        // Both buttons below save "onboarding done" so the slides never appear again.
        // Only "start tutorial" also turns on the guided tutorials; Skip does not.
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                // Skip -> Main Menu without the guided tutorial.
                onSkip = {
                    onboardingPrefs.setOnboardingDone(true)
                    navController.navigate(Routes.MAIN_MENU) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                },
                // start tutorial -> Main Menu, with all guided tutorials turned on.
                onStartTutorial = {
                    onboardingPrefs.setOnboardingDone(true)
                    onboardingPrefs.setAllTutorialsPending(true)
                    navController.navigate(Routes.MAIN_MENU) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        // ---- Main Menu ----
        // ===== TUTORIAL NAVIGATION AND STEPS: MAIN MENU TUTORIAL WIRING =====
        // showTutorial starts from the saved flag. Finish (OK on the last step) turns off only this screen's
        // flag. Close (X) turns off ALL tutorials. The steps themselves are in ui/screens/MainMenuScreen.kt.
        composable(Routes.MAIN_MENU) {
            var showTutorial by remember {
                mutableStateOf(onboardingPrefs.isTutorialPending(OnboardingPrefs.TUTORIAL_MAIN_MENU))
            }
            MainMenuScreen(
                // NAVIGATION: Translator -> the existing Translation screen (Text to Sign).
                onOpenTranslator = { navController.navigate(Routes.TEXT_TO_SIGN) },
                // NAVIGATION: Learning Module -> the existing Learning Menu.
                onOpenLearning = { navController.navigate(Routes.LEARNING_HOME) },
                showTutorial = showTutorial,
                onTutorialFinished = {
                    onboardingPrefs.setTutorialPending(OnboardingPrefs.TUTORIAL_MAIN_MENU, false)
                    showTutorial = false
                },
                // The X button cancels the whole tutorial (all screens).
                onTutorialDismissed = {
                    onboardingPrefs.setAllTutorialsPending(false)
                    showTutorial = false
                }
            )
        }

        // ---- Translation: Text/Speech to Sign (default screen) ----
        // ===== TUTORIAL NAVIGATION AND STEPS: TEXT -> SIGN WALKTHROUGH WIRING =====
        // Shown once, the first time this screen opens after "start tutorial". Its last step points to the
        // Sign -> Text toggle; the Sign -> Text walkthrough then appears when the user switches screens.
        // The steps (including the one-time Learning Module shortcut step) are in ui/screens/TranslatorTutorials.kt.
        composable(Routes.TEXT_TO_SIGN) {
            var showTutorial by remember {
                mutableStateOf(onboardingPrefs.isTutorialPending(OnboardingPrefs.TUTORIAL_TEXT_TO_SIGN))
            }
            TextToSignScreen(
                // NAVIGATION: Toggle -> reverse translation. The old translation screen is replaced,
                // so switching back and forth never piles up screens.
                onSwitchToSignToText = {
                    navController.navigate(Routes.SIGN_TO_TEXT) {
                        popUpTo(Routes.TEXT_TO_SIGN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onOpenLearningMenu = { navController.navigate(Routes.LEARNING_HOME) },
                showTutorial = showTutorial,
                onTutorialFinished = {
                    onboardingPrefs.setTutorialPending(OnboardingPrefs.TUTORIAL_TEXT_TO_SIGN, false)
                    showTutorial = false
                },
                onTutorialDismissed = {
                    onboardingPrefs.setAllTutorialsPending(false)
                    showTutorial = false
                }
            )
        }

        // ---- Translation: Sign to Text/Speech ----
        // ===== TUTORIAL NAVIGATION AND STEPS: SIGN -> TEXT WALKTHROUGH WIRING =====
        // Shown once, the first time this screen opens after "start tutorial" (it does not repeat the
        // Learning Module shortcut explanation). Steps: ui/screens/TranslatorTutorials.kt.
        composable(Routes.SIGN_TO_TEXT) {
            var showTutorial by remember {
                mutableStateOf(onboardingPrefs.isTutorialPending(OnboardingPrefs.TUTORIAL_SIGN_TO_TEXT))
            }
            SignToTextScreen(
                onSwitchToTextToSign = {
                    navController.navigate(Routes.TEXT_TO_SIGN) {
                        popUpTo(Routes.SIGN_TO_TEXT) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onOpenLearningMenu = { navController.navigate(Routes.LEARNING_HOME) },
                showTutorial = showTutorial,
                onTutorialFinished = {
                    onboardingPrefs.setTutorialPending(OnboardingPrefs.TUTORIAL_SIGN_TO_TEXT, false)
                    showTutorial = false
                },
                onTutorialDismissed = {
                    onboardingPrefs.setAllTutorialsPending(false)
                    showTutorial = false
                }
            )
        }

        // ---- Learning Menu ----
        // ===== TUTORIAL NAVIGATION AND STEPS: LEARNING HOME TUTORIAL WIRING =====
        // Same idea as the other screens: Finish clears this screen's flag, X clears all tutorials.
        // The steps are in ui/screens/LearningHomeScreen.kt.
        composable(Routes.LEARNING_HOME) {
            var showTutorial by remember {
                mutableStateOf(onboardingPrefs.isTutorialPending(OnboardingPrefs.TUTORIAL_LEARNING_HOME))
            }
            LearningHomeScreen(
                onBack = { navController.popBackStack() },
                onOpenAlphabet = { navController.navigate(Routes.ALPHABET) },
                onOpenNumbers = { navController.navigate(Routes.NUMBERS) },
                onOpenTutorials = { navController.navigate(Routes.TUTORIALS) },
                // NAVIGATION: FSL Phrases card -> the phrase category screen.
                onOpenPhrases = { navController.navigate(Routes.PHRASE_CATEGORIES) },
                showTutorial = showTutorial,
                onTutorialFinished = {
                    onboardingPrefs.setTutorialPending(OnboardingPrefs.TUTORIAL_LEARNING_HOME, false)
                    showTutorial = false
                },
                onTutorialDismissed = {
                    onboardingPrefs.setAllTutorialsPending(false)
                    showTutorial = false
                }
            )
        }

        // ---- FSL Phrases: choose a category ----
        // ===== TUTORIAL NAVIGATION AND STEPS: PHRASE CATEGORY WALKTHROUGH WIRING =====
        // Shown once, the first time the FSL Phrases screen opens after "start tutorial".
        // Steps: ui/screens/LearningModuleTutorials.kt.
        composable(Routes.PHRASE_CATEGORIES) {
            var showTutorial by remember {
                mutableStateOf(onboardingPrefs.isTutorialPending(OnboardingPrefs.TUTORIAL_PHRASE_CATEGORIES))
            }
            PhraseCategoriesScreen(
                onBack = { navController.popBackStack() },
                // NAVIGATION: Category card -> that category's lesson list (existing Tutorials screen, filtered).
                onOpenCategory = { category -> navController.navigate(Routes.category(category)) },
                showTutorial = showTutorial,
                onTutorialFinished = {
                    onboardingPrefs.setTutorialPending(OnboardingPrefs.TUTORIAL_PHRASE_CATEGORIES, false)
                    showTutorial = false
                },
                onTutorialDismissed = {
                    onboardingPrefs.setAllTutorialsPending(false)
                    showTutorial = false
                }
            )
        }

        // ---- Alphabet: choose a letter ----
        // ===== TUTORIAL NAVIGATION AND STEPS: ALPHABET WALKTHROUGH WIRING =====
        // Shown once, the first time the letter selection opens after "start tutorial".
        // Numbers (below) intentionally has no tutorial. Steps: ui/screens/LearningModuleTutorials.kt.
        composable(Routes.ALPHABET) {
            var showTutorial by remember {
                mutableStateOf(onboardingPrefs.isTutorialPending(OnboardingPrefs.TUTORIAL_ALPHABET_SELECT))
            }
            ItemSelectionScreen(
                title = "Choose what letter to learn",
                items = SampleData.alphabet,
                onItemSelected = { index ->
                    navController.navigate(Routes.practice(Routes.TYPE_ALPHABET, index))
                },
                onBack = { navController.popBackStack() },
                onReturnToTranslation = returnToTranslation,
                showTutorial = showTutorial,
                onTutorialFinished = {
                    onboardingPrefs.setTutorialPending(OnboardingPrefs.TUTORIAL_ALPHABET_SELECT, false)
                    showTutorial = false
                },
                onTutorialDismissed = {
                    onboardingPrefs.setAllTutorialsPending(false)
                    showTutorial = false
                }
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
        // LEARNING PHRASES CATEGORY UI: opened from a category card; the layout is the existing lesson list.
        composable(
            route = Routes.CATEGORY,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            // ===== TUTORIAL NAVIGATION AND STEPS: PHRASE LESSON LIST WALKTHROUGH WIRING =====
            // Shown once, the first time any category's lesson list opens.
            var showTutorial by remember {
                mutableStateOf(onboardingPrefs.isTutorialPending(OnboardingPrefs.TUTORIAL_PHRASE_LESSONS))
            }
            TutorialsScreen(
                onViewLesson = { lessonId -> navController.navigate(Routes.lesson(lessonId)) },
                onBack = { navController.popBackStack() },
                onReturnToTranslation = returnToTranslation,
                category = backStackEntry.arguments?.getString("category"),
                showTutorial = showTutorial,
                onTutorialFinished = {
                    onboardingPrefs.setTutorialPending(OnboardingPrefs.TUTORIAL_PHRASE_LESSONS, false)
                    showTutorial = false
                },
                onTutorialDismissed = {
                    onboardingPrefs.setAllTutorialsPending(false)
                    showTutorial = false
                }
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
