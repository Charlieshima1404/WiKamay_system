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
        Lesson(4, "Basic responses", "Yes / Oo",
            "Placeholder description. Replace this text with how to sign \"Yes\" in FSL."),
        Lesson(5, "Basic responses", "No / Hindi",
            "Placeholder description. Replace this text with how to sign \"No\" in FSL.")
    )
}
