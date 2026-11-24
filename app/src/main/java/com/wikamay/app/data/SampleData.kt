
package com.wikamay.app.data

/**
 * PURPOSE: One tutorial lesson shown in the Tutorials list and the lesson details screen.
 */
data class Lesson(
    val id: Int,              // PURPOSE: Unique number of the lesson (1, 2, 3, ...).
    val category: String,      // PURPOSE: Group the lesson belongs to (e.g. "Greetings").
    val signName: String,      // PURPOSE: Name of the sign shown as the lesson title.
)

/**
 * PURPOSE: All dummy (mock) content for the prototype lives here.
 * PLACEHOLDER: In future development this data can be replaced by the real FSL content
 * (JSON / bundled assets / database). The screens only read from this object.
 */
object SampleData {

    // FSL alphabet: A-Z. J and Z are dynamic signs in the recognition model.
    val alphabet: List<String> = ('A'..'Z').map { it.toString() }

    // Use the exact labels from sign_labels.json, not numeric strings.
    val numbers: List<String> = listOf(
        "1", "2", "3", "4", "5",
        "6", "7", "8", "9", "10"
    )

    val sampleTranslations: List<String> = listOf(
        "Kamusta",
        "Salamat po",
        "Magandang umaga",
        "Ako si WiKamay"
    )

    // Tutorial vocabulary grouped using the existing Learning Home categories.
    private val lessonGroups = listOf(

        "Greetings" to listOf(
            "GOOD MORNING",
            "GOOD AFTERNOON",
            "GOOD EVENING",
            "HELLO",
            "HOW ARE YOU",
            "IM FINE",
            "NICE TO MEET YOU",
            "THANK YOU",
            "YOURE WELCOME",
            "SEE YOU TOMORROW"
        ),

        "Survival" to listOf(
            "UNDERSTAND",
            "DON'T UNDERSTAND",
            "KNOW",
            "DON'T KNOW",
            "NO",
            "YES",
            "WRONG",
            "CORRECT",
            "SLOW",
            "FAST",
            "DEAF",
            "HARD OF HEARING",
            "WHEELCHAIR PERSON",
            "BLIND",
            "DEAF BLIND"
        ),

        "Calendar" to listOf(
            "JANUARY",
            "FEBRUARY",
            "MARCH",
            "APRIL",
            "MAY",
            "JUNE",
            "JULY",
            "AUGUST",
            "SEPTEMBER",
            "OCTOBER",
            "NOVEMBER",
            "DECEMBER"
        ),

        "Days" to listOf(
            "MONDAY",
            "TUESDAY",
            "WEDNESDAY",
            "THURSDAY",
            "FRIDAY",
            "SATURDAY",
            "SUNDAY",
            "TODAY",
            "TOMORROW",
            "YESTERDAY"
        ),

        "Family" to listOf(
            "FATHER",
            "MOTHER",
            "SON",
            "DAUGHTER",
            "GRANDFATHER",
            "GRANDMOTHER",
            "UNCLE",
            "AUNTIE",
            "COUSIN",
            "PARENTS"
        ),

        "Relationships" to listOf(
            "BOY",
            "GIRL",
            "MAN",
            "WOMAN",
            "MARRIED"
        ),

        "Color" to listOf(
            "BLUE",
            "GREEN",
            "RED",
            "BROWN",
            "BLACK",
            "WHITE",
            "YELLOW",
            "ORANGE",
            "GRAY",
            "PINK",
            "VIOLET",
            "LIGHT",
            "DARK"
        ),

        "Food" to listOf(
            "BREAD",
            "EGG",
            "FISH",
            "MEAT",
            "CHICKEN",
            "SPAGHETTI",
            "RICE",
            "LONGANISA",
            "SHRIMP",
            "CRAB"
        ),

        "Drink" to listOf(
            "HOT",
            "COLD",
            "JUICE",
            "MILK",
            "COFFEE",
            "TEA",
            "BEER",
            "WINE",
            "SUGAR",
            "NO SUGAR"
        )
    )

    // IDs are generated sequentially and remain unique.
    val lessons: List<Lesson> = lessonGroups
        .flatMap { (category, signs) ->
            signs.map { signName -> category to signName }
        }
        .mapIndexed { index, (category, signName) ->
            Lesson(
                id = index + 1,
                category = category,
                signName = signName
            )
        }
}
