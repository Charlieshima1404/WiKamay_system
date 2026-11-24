package com.wikamay.app.data

import android.content.Context

// ===== FIRST-LAUNCH UI LOGIC: LOCAL FLAGS (ONBOARDING + TUTORIAL) =====
// This file decides, using small flags saved on the phone, whether the user already saw the
// onboarding slides and which guided tutorials are still waiting to be shown.
// It uses Android's built-in SharedPreferences, so no extra library is needed.
// It only remembers true/false values for the UI. It does not touch translation, camera,
// recognition, or any learning data.
//
// HOW IT WORKS (simple version):
//  - First launch: "onboarding_done" is false -> the app shows the onboarding slides.
//  - The user taps Skip or "start tutorial" -> "onboarding_done" becomes true -> never shown again.
//  - "start tutorial" also turns every tutorial flag below on. Each screen shows its walkthrough once,
//    then turns its own flag off.
//
// HOW TO TEST AGAIN: clear the app data (Android Settings > Apps > WiKamay > Storage > Clear data),
// or call setOnboardingDone(false) / setAllTutorialsPending(true) temporarily.
//
// WHERE THE FLAGS ARE USED: navigation/WiKamayNavGraph.kt (search for "FIRST-LAUNCH UI LOGIC").
class OnboardingPrefs(context: Context) {

    private val prefs =
        context.applicationContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    // ===== FIRST-LAUNCH UI LOGIC: ONBOARDING VISIBILITY =====
    // true  = onboarding was finished or skipped (do not show it again).
    // false = first launch (show the onboarding slides).
    fun isOnboardingDone(): Boolean = prefs.getBoolean(KEY_ONBOARDING_DONE, false)

    fun setOnboardingDone(done: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING_DONE, done).apply()
    }

    // ===== FIRST-LAUNCH UI LOGIC: TUTORIAL FLAGS =====
    // true = this screen's walkthrough was requested and has not been completed or closed yet.
    fun isTutorialPending(tutorialKey: String): Boolean = prefs.getBoolean(tutorialKey, false)

    fun setTutorialPending(tutorialKey: String, pending: Boolean) {
        prefs.edit().putBoolean(tutorialKey, pending).apply()
    }

    // Turns EVERY tutorial on (used by "start tutorial") or off (used when the user closes a tutorial with X).
    fun setAllTutorialsPending(pending: Boolean) {
        val editor = prefs.edit()
        ALL_TUTORIALS.forEach { editor.putBoolean(it, pending) }
        editor.apply()
    }

    companion object {
        private const val FILE_NAME = "wikamay_onboarding"
        private const val KEY_ONBOARDING_DONE = "onboarding_done"

        // ===== TUTORIAL NAVIGATION AND STEPS: TUTORIAL KEYS =====
        // One key per screen that has a walkthrough. To add a walkthrough to another screen:
        // 1) add a key here, 2) add it to ALL_TUTORIALS, 3) use it in WiKamayNavGraph.kt like the others.
        const val TUTORIAL_MAIN_MENU = "tutorial_main_menu"
        const val TUTORIAL_TEXT_TO_SIGN = "tutorial_text_to_sign"
        const val TUTORIAL_SIGN_TO_TEXT = "tutorial_sign_to_text"
        const val TUTORIAL_LEARNING_HOME = "tutorial_learning_home"
        const val TUTORIAL_ALPHABET_SELECT = "tutorial_alphabet_select"
        const val TUTORIAL_PHRASE_CATEGORIES = "tutorial_phrase_categories"
        const val TUTORIAL_PHRASE_LESSONS = "tutorial_phrase_lessons"

        // The order here is only for reading; the order the user sees is decided by how they move through the app.
        private val ALL_TUTORIALS = listOf(
            TUTORIAL_MAIN_MENU,
            TUTORIAL_TEXT_TO_SIGN,
            TUTORIAL_SIGN_TO_TEXT,
            TUTORIAL_LEARNING_HOME,
            TUTORIAL_ALPHABET_SELECT,
            TUTORIAL_PHRASE_CATEGORIES,
            TUTORIAL_PHRASE_LESSONS
        )
    }
}
