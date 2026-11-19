package com.wikamay.app.ml

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.ImageProcessingOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult

class HandLandmark(context: Context, private val onResult: (HandLandmarkerResult) -> Unit, private val onError: (String) -> Unit) : AutoCloseable {

    companion object {
        private const val MODEL_FILE = "hand_landmarker.task"
        private const val MAX_HANDS = 2
        private const val TAG = "HandLandmarker"
    }

    private val handLandmarker: HandLandmarker

    private var lastTimestampMs = 0L

    init {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath(MODEL_FILE)
            .build()

        val options = HandLandmarker.HandLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setNumHands(MAX_HANDS)
            .setMinHandDetectionConfidence(0.5f)
            .setMinHandPresenceConfidence(0.5f)
            .setMinTrackingConfidence(0.5f)
            .setResultListener { result, input ->
                Log.d(
                    TAG,
                    "Landmark dimensions: ${result.landmarks().firstOrNull()?.size}"
                )
                onResult(result)
            }
            .setErrorListener { error ->
                val message = error.message ?: "Unknown MediaPipe error"
                Log.e(TAG, message)
                onError(message)
            }
            .build()

        handLandmarker = HandLandmarker.createFromOptions(
            context.applicationContext,
            options
        )
    }

    fun detect(imageProxy: ImageProxy) {
        val bitmap = imageProxy.toBitmap()

        val timestampMs = maxOf(
            imageProxy.imageInfo.timestamp / 1_000_000L,
            lastTimestampMs + 1L
        )

        lastTimestampMs = timestampMs

        detect(
            bitmap = bitmap,
            rotationDegrees = imageProxy.imageInfo.rotationDegrees,
            timestampMs = timestampMs
        )

        Log.d(
            "CameraRotation",
            "Rotation: ${imageProxy.imageInfo.rotationDegrees}"
        )
    }

    fun detect(bitmap: Bitmap,  rotationDegrees: Int, timestampMs: Long){
        val argbBitmap = if (bitmap.config == Bitmap.Config.ARGB_8888) {
            bitmap
        } else {
            bitmap.copy(Bitmap.Config.ARGB_8888, false)
                ?: throw IllegalArgumentException("Unable to convert camera frame to ARGB_8888")
        }

        val mpImage = BitmapImageBuilder(argbBitmap).build()

        val imageOptions = ImageProcessingOptions.builder()
            .setRotationDegrees(rotationDegrees)
            .build()

        handLandmarker.detectAsync(
            mpImage,
            imageOptions,
            timestampMs
        )
    }

    override fun close() {
        handLandmarker.close()
    }
}