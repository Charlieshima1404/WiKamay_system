package com.wikamay.app.data

/**
 * PURPOSE: One tutorial lesson shown in the Tutorials list and the lesson details screen.
 */
data class Lesson(
    val id: Int,              // PURPOSE: Unique number of the lesson (1, 2, 3, ...).
    val category: String,     // PURPOSE: Group the lesson belongs to (e.g. "Greetings").
    val signName: String,     // PURPOSE: Name of the sign shown as the lesson title.
    val description: String   // PURPOSE: Short explanation of the sign.
)

/**
 * PURPOSE: All dummy (mock) content for the prototype lives here.
 * PLACEHOLDER: In future development this data can be replaced by the real FSL content
 * (JSON / bundled assets / database). The screens only read from this object.
 */
object SampleData {

    // EDITABLE: Letters shown in the FSL Alphabet section.
    val alphabet: List<String> = ('A'..'Z').map { it.toString() }

    // EDITABLE: Numbers shown in the Numbers section (change the range to add more).
    val numbers: List<String> = (1..10).map { it.toString() }

    // EDITABLE: Fake results shown by the Sign-to-Text screen each time the scan button is tapped.
    val sampleTranslations: List<String> = listOf(
        "Kamusta",
        "Salamat po",
        "Magandang umaga",
        "Ako si WiKamay"
    )

    // EDITABLE: Tutorial lessons. Replace the descriptions with real sign instructions later.
    val lessons: List<Lesson> = listOf(
        Lesson(1, "Greetings", "Hello / Kamusta",
            "Placeholder description. Replace this text with how to sign \"Hello\" in FSL."),
        Lesson(2, "Greetings", "Thank you / Salamat",
            "Placeholder description. Replace this text with how to sign \"Thank you\" in FSL."),
        Lesson(3, "Greetings", "Good morning / Magandang umaga",
            "Placeholder description. Replace this text with how to sign \"Good morning\" in FSL."),
        Lesson(4, "Survival", "Yes / Oo",
            "Placeholder description. Replace this text with how to sign \"Yes\" in FSL."),
        Lesson(5, "Survival", "No / Hindi",
            "Placeholder description. Replace this text with how to sign \"No\" in FSL."),

        // ==========================================
        // NEW CATEGORIES (shown on the Learning Menu cards)
        // EDIT HERE: The first value after the id is the category name. It must match the card title in
        // LEARN_CATEGORIES (LearningHomeScreen.kt): Greetings, Survival, Calendar, Days, Family,
        // Relationships, Color, Food, Drink. Add a Lesson(...) line to add a sign; ids must be unique.
        // PLACEHOLDER: Words and Filipino translations below are starter vocabulary. Check them against
        // your FSL source and replace the descriptions with real sign instructions.
        // ==========================================
        Lesson(6, "Survival", "Help / Tulong",
            "Placeholder description. Replace this text with how to sign \"Help\" in FSL."),
        Lesson(7, "Survival", "Sorry / Pasensya",
            "Placeholder description. Replace this text with how to sign \"Sorry\" in FSL."),
        Lesson(8, "Calendar", "Today / Ngayon",
            "Placeholder description. Replace this text with how to sign \"Today\" in FSL."),
        Lesson(9, "Calendar", "Tomorrow / Bukas",
            "Placeholder description. Replace this text with how to sign \"Tomorrow\" in FSL."),
        Lesson(10, "Calendar", "Yesterday / Kahapon",
            "Placeholder description. Replace this text with how to sign \"Yesterday\" in FSL."),
        Lesson(11, "Days", "Monday / Lunes",
            "Placeholder description. Replace this text with how to sign \"Monday\" in FSL."),
        Lesson(12, "Days", "Tuesday / Martes",
            "Placeholder description. Replace this text with how to sign \"Tuesday\" in FSL."),
        Lesson(13, "Days", "Wednesday / Miyerkules",
            "Placeholder description. Replace this text with how to sign \"Wednesday\" in FSL."),
        Lesson(14, "Days", "Thursday / Huwebes",
            "Placeholder description. Replace this text with how to sign \"Thursday\" in FSL."),
        Lesson(15, "Days", "Friday / Biyernes",
            "Placeholder description. Replace this text with how to sign \"Friday\" in FSL."),
        Lesson(16, "Days", "Saturday / Sabado",
            "Placeholder description. Replace this text with how to sign \"Saturday\" in FSL."),
        Lesson(17, "Days", "Sunday / Linggo",
            "Placeholder description. Replace this text with how to sign \"Sunday\" in FSL."),
        Lesson(18, "Family", "Mother / Nanay",
            "Placeholder description. Replace this text with how to sign \"Mother\" in FSL."),
        Lesson(19, "Family", "Father / Tatay",
            "Placeholder description. Replace this text with how to sign \"Father\" in FSL."),
        Lesson(20, "Family", "Sibling / Kapatid",
            "Placeholder description. Replace this text with how to sign \"Sibling\" in FSL."),
        Lesson(21, "Family", "Grandmother / Lola",
            "Placeholder description. Replace this text with how to sign \"Grandmother\" in FSL."),
        Lesson(22, "Family", "Grandfather / Lolo",
            "Placeholder description. Replace this text with how to sign \"Grandfather\" in FSL."),
        Lesson(23, "Relationships", "Friend / Kaibigan",
            "Placeholder description. Replace this text with how to sign \"Friend\" in FSL."),
        Lesson(24, "Relationships", "Partner / Kasintahan",
            "Placeholder description. Replace this text with how to sign \"Partner\" in FSL."),
        Lesson(25, "Relationships", "Spouse / Asawa",
            "Placeholder description. Replace this text with how to sign \"Spouse\" in FSL."),
        Lesson(26, "Color", "Red / Pula",
            "Placeholder description. Replace this text with how to sign \"Red\" in FSL."),
        Lesson(27, "Color", "Blue / Asul",
            "Placeholder description. Replace this text with how to sign \"Blue\" in FSL."),
        Lesson(28, "Color", "Green / Berde",
            "Placeholder description. Replace this text with how to sign \"Green\" in FSL."),
        Lesson(29, "Color", "Yellow / Dilaw",
            "Placeholder description. Replace this text with how to sign \"Yellow\" in FSL."),
        Lesson(30, "Color", "White / Puti",
            "Placeholder description. Replace this text with how to sign \"White\" in FSL."),
        Lesson(31, "Color", "Black / Itim",
            "Placeholder description. Replace this text with how to sign \"Black\" in FSL."),
        Lesson(32, "Food", "Rice / Kanin",
            "Placeholder description. Replace this text with how to sign \"Rice\" in FSL."),
        Lesson(33, "Food", "Bread / Tinapay",
            "Placeholder description. Replace this text with how to sign \"Bread\" in FSL."),
        Lesson(34, "Food", "Egg / Itlog",
            "Placeholder description. Replace this text with how to sign \"Egg\" in FSL."),
        Lesson(35, "Food", "Fish / Isda",
            "Placeholder description. Replace this text with how to sign \"Fish\" in FSL."),
        Lesson(36, "Food", "Chicken / Manok",
            "Placeholder description. Replace this text with how to sign \"Chicken\" in FSL."),
        Lesson(37, "Drink", "Water / Tubig",
            "Placeholder description. Replace this text with how to sign \"Water\" in FSL."),
        Lesson(38, "Drink", "Milk / Gatas",
            "Placeholder description. Replace this text with how to sign \"Milk\" in FSL."),
        Lesson(39, "Drink", "Coffee / Kape",
            "Placeholder description. Replace this text with how to sign \"Coffee\" in FSL."),
        Lesson(40, "Drink", "Tea / Tsaa",
            "Placeholder description. Replace this text with how to sign \"Tea\" in FSL.")
    )
}
