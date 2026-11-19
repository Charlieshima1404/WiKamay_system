
package com.wikamay.app.ml

class EnglishSentenceBuilder {

    private var candidateLabel = ""
    private var candidateCount = 0
    private var lastCommittedLabel = ""

    private val recognizedSigns = mutableListOf<String>()

    fun accept(prediction: Prediction): String? {
        val label = prediction.label.trim()

        if (label.isEmpty()) return null

        if (label == candidateLabel) {
            candidateCount++
        } else {
            candidateLabel = label
            candidateCount = 1
        }

        // PURPOSE: Requires three consistent predictions
        if (candidateCount < 3) return null

        // PURPOSE: Avoids repeatedly adding a held sign
        if (label == lastCommittedLabel) return null

        recognizedSigns.add(label)
        lastCommittedLabel = label

        return recognizedSigns.joinToString(" ")
    }

    fun reset() {
        candidateLabel = ""
        candidateCount = 0
        lastCommittedLabel = ""
        recognizedSigns.clear()
    }
}
