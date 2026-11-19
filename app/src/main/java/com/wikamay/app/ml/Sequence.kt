package com.wikamay.app.ml

import android.content.Context
import android.os.SystemClock
import java.util.ArrayDeque
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

data class RecognitionUpdate(
    val staticPrediction: Prediction?,
    val dynamicPrediction: Prediction?,
    val selectedPrediction: Prediction?,
    val hasHand: Boolean,
    val dynamicReady: Boolean
)

class Sequence(
    context: Context,
    private val onUpdate: (RecognitionUpdate) -> Unit,
    private val onError: (String) -> Unit
) : AutoCloseable {

    companion object {
        private const val FRAME_COUNT = 30
        private const val FEATURE_COUNT = 127

        // Limit how often each prediction runs.
        private const val DYNAMIC_WINDOW_MS = 2000L
        private const val MIN_WINDOW_SPAN_MS = 1800L
        private const val INFERENCE_INTERVAL_MS = 500L
    }

    private val recognition = Recognition(context.applicationContext)

    private val frameBuffer =
        ArrayDeque<Pair<Long, FloatArray>>()
    private val lock = Any()

    private val inferenceExecutor = Executors.newSingleThreadExecutor()
    private val inferenceRunning = AtomicBoolean(false)

    private var lastInference = 0L
    private var closed = false
    private var sessionActive = false
    private var sessionVersion = 0L

    // PURPOSE: To start sign recognition
    fun startSession() {
        synchronized(lock) {
            if (closed) return

            frameBuffer.clear()
            lastInference = 0L

            sessionVersion++
            sessionActive = true
        }
    }

    // PURPOSE: To stop sign recognition
    fun stopSession() {
        synchronized(lock) {
            sessionActive = false
            sessionVersion++

            frameBuffer.clear()
            lastInference = 0L
        }
    }

    // PURPOSE: Receives the extracted hand landmarks
    // PURPOSE: Receives extracted hand landmarks and runs recognition.
    fun processFrame(frame: ExtractedHandFrame) {
        val currentFrame = FloatArray(FEATURE_COUNT)

        frame.features.copyInto(
            destination = currentFrame,
            endIndex = frame.features.size.coerceAtMost(126)
        )

        currentFrame[126] = if (frame.detected) 1f else 0f

        var staticInput: FloatArray? = null
        var dynamicInput: Array<FloatArray>? = null

        var currentSessionVersion = 0L
        var shouldRunInference = false

        synchronized(lock) {
            if (closed || !sessionActive) return

            currentSessionVersion = sessionVersion

            val now = SystemClock.elapsedRealtime()

            // Store every frame, including frames without a detected hand.
            frameBuffer.addLast(now to currentFrame)

            // Keep the latest two seconds of frames.
            val windowStart = now - DYNAMIC_WINDOW_MS

            while (
                frameBuffer.isNotEmpty() &&
                frameBuffer.first().first < windowStart
            ) {
                frameBuffer.removeFirst()
            }

            val recentFrames = frameBuffer.toList()

            val windowSpan = if (recentFrames.size >= 2) {
                recentFrames.last().first - recentFrames.first().first
            } else {
                0L
            }

            val dynamicReady =
                recentFrames.size >= FRAME_COUNT &&
                        windowSpan >= MIN_WINDOW_SPAN_MS

            val intervalReached =
                now - lastInference >= INFERENCE_INTERVAL_MS

            val runStatic =
                frame.detected && intervalReached

            val runDynamic =
                dynamicReady && intervalReached

            if (!runStatic && !runDynamic) return

            if (!inferenceRunning.compareAndSet(false, true)) return

            lastInference = now
            shouldRunInference = true

            if (runStatic) {
                staticInput = frame.features.copyOf()
            }

            if (runDynamic) {
                dynamicInput = sampleFrames(recentFrames)
            }
        }

        if (!shouldRunInference) return

        inferenceExecutor.execute {
            try {
                val staticPrediction = staticInput?.let {
                    recognition.predictStatic(it)
                }

                val dynamicPrediction = dynamicInput?.let {
                    recognition.predictDynamic(it)
                }

                // Select the stronger available candidate.
                val selectedPrediction = when {
                    staticPrediction == null -> dynamicPrediction

                    dynamicPrediction == null -> staticPrediction

                    staticPrediction.confidence >=
                            dynamicPrediction.confidence -> staticPrediction

                    else -> dynamicPrediction
                }

                val shouldPublish = synchronized(lock) {
                    !closed &&
                            sessionActive &&
                            sessionVersion == currentSessionVersion
                }

                if (shouldPublish) {
                    onUpdate(
                        RecognitionUpdate(
                            staticPrediction = staticPrediction,
                            dynamicPrediction = dynamicPrediction,
                            selectedPrediction = selectedPrediction,
                            hasHand = frame.detected,
                            dynamicReady = dynamicPrediction != null
                        )
                    )
                }

            } catch (exception: Exception) {
                onError(
                    exception.message ?: "Recognition failed."
                )
            } finally {
                inferenceRunning.set(false)
            }
        }
    }

    // PURPOSE: Select 30 evenly spaced frames from the time window.
    private fun sampleFrames(
        frames: List<Pair<Long, FloatArray>>
    ): Array<FloatArray> {

        val firstTimestamp = frames.first().first
        val lastTimestamp = frames.last().first

        val duration = lastTimestamp - firstTimestamp

        return Array(FRAME_COUNT) { index ->

            val targetTimestamp =
                firstTimestamp +
                        (duration * index / (FRAME_COUNT - 1))

            val closestFrame = frames.minByOrNull {
                kotlin.math.abs(it.first - targetTimestamp)
            } ?: error("No frames available for dynamic recognition.")

            closestFrame.second.copyOf()
        }
    }

    override fun close() {
        synchronized(lock) {
            if (closed) return
            closed = true
        }

        // Close the interpreter after queued inference work finishes.
        inferenceExecutor.execute {
            recognition.close()
        }

        inferenceExecutor.shutdown()
    }
}
