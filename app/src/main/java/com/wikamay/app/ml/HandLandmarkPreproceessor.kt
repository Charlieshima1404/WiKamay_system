package com.wikamay.app.ml

import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult

data class ExtractedHandFrame(
    val features: FloatArray,
    val detected: Boolean
)

object HandLandmarkPreprocessor {

    private const val HAND_COUNT = 2
    private const val LANDMARK_COUNT = 21
    private const val COORDINATES = 3
    const val FEATURE_COUNT = 126

    fun extract(
        result: HandLandmarkerResult
    ): ExtractedHandFrame {

        val features = FloatArray(FEATURE_COUNT)

        val worldLandmarks = result.worldLandmarks()
        val handedness = result.handedness()

        if (worldLandmarks.isEmpty()) {
            return ExtractedHandFrame(
                features = features,
                detected = false
            )
        }

        for (handIndex in worldLandmarks.indices) {

            if (handIndex >= HAND_COUNT) break

            val handCategory = handedness
                .getOrNull(handIndex)
                ?.firstOrNull()
                ?.categoryName()
                ?: continue

            val handSlot = if (
                handCategory.equals("Left", ignoreCase = true)
            ) {
                0
            } else {
                1
            }

            val landmarks = worldLandmarks[handIndex]

            for (landmarkIndex in 0 until LANDMARK_COUNT) {

                if (landmarkIndex >= landmarks.size) break

                val landmark = landmarks[landmarkIndex]

                val offset =
                    (handSlot * LANDMARK_COUNT + landmarkIndex) *
                            COORDINATES

                // Use WORLD coordinates directly.
                // Do not normalize or convert them.

                features[offset] = landmark.x()
                features[offset + 1] = landmark.y()
                features[offset + 2] = landmark.z()
            }
        }

        return ExtractedHandFrame(
            features = features,
            detected = true
        )
    }
}