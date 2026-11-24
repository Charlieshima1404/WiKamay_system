package com.wikamay.app.navigation

/**
 * NAVIGATION: The "address" of every screen. The NavGraph uses these names to open screens.
 * Screens with extra information (which letter, which lesson) have {placeholders} in their route.
 */
object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val MAIN_MENU = "main_menu"
    const val TEXT_TO_SIGN = "text_to_sign"
    const val SIGN_TO_TEXT = "sign_to_text"
    const val LEARNING_HOME = "learning_home"
    const val ALPHABET = "alphabet"
    const val NUMBERS = "numbers"
    const val TUTORIALS = "tutorials"
    // PURPOSE: FSL Phrases screen (grid of phrase categories).
    const val PHRASE_CATEGORIES = "phrase_categories"

    // PURPOSE: Lesson list of ONE category (Greetings, Survival, ...): {category} is the category name.
    const val CATEGORY = "category/{category}"

    // PURPOSE: Practice screen: {type} is "alphabet" or "numbers", {index} is the selected item position.
    const val PRACTICE = "practice/{type}/{index}"
    // PURPOSE: Lesson details screen: {id} is the lesson number.
    const val LESSON = "lesson/{id}"

    const val TYPE_ALPHABET = "alphabet"
    const val TYPE_NUMBERS = "numbers"

    // FUNCTION: Builds the real route text, e.g. practice("alphabet", 2) -> "practice/alphabet/2".
    fun practice(type: String, index: Int) = "practice/$type/$index"

    // FUNCTION: Builds the real route text, e.g. lesson(3) -> "lesson/3".
    fun lesson(id: Int) = "lesson/$id"

    // FUNCTION: Builds the real route text, e.g. category("Food") -> "category/Food".
    fun category(name: String) = "category/$name"
}
