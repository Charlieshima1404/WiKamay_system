package com.wikamay.app.ml

import android.content.Context
import org.json.JSONObject
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

data class Prediction(
    val label: String,
    val confidence: Float,
    val classIndex: Int
)

class Recognition(context: Context) : AutoCloseable {

    companion object {
        private const val MODEL_FILE = "WiKamay_Two_Branch_Model_Builtin.tflite"
        private const val LABELS_FILE = "sign_labels.json"

        private const val FRAME_COUNT = 30
        private const val FEATURE_COUNT = 127
        private const val LANDMARK_FEATURE_COUNT = 126
        private const val CLASS_COUNT = 131

        private val STATIC_CLASS_INDICES =
            ((105..113) + (115..129)).toSet()

        private val DYNAMIC_CLASS_INDICES =
            (0 until CLASS_COUNT).filterNot {
                it in STATIC_CLASS_INDICES
            }.toSet()
    }

    private val labels: List<String>
    private val interpreter: Interpreter

    init {
        val appContext = context.applicationContext

        labels = loadLabels(appContext)
        require(labels.size == CLASS_COUNT) {
            "Expected $CLASS_COUNT labels, but found ${labels.size}."
        }

        val modelBuffer = loadModel(appContext)

        interpreter = Interpreter(
            modelBuffer,
            Interpreter.Options().apply {
                setNumThreads(2)
            }
        )

        val inputShape = interpreter.getInputTensor(0).shape()
        val outputShape = interpreter.getOutputTensor(0).shape()

        require(inputShape.contentEquals(intArrayOf(1, FRAME_COUNT, FEATURE_COUNT))) {
            "Unexpected model input shape: ${inputShape.contentToString()}"
        }

        require(outputShape.contentEquals(intArrayOf(1, CLASS_COUNT))) {
            "Unexpected model output shape: ${outputShape.contentToString()}"
        }
    }

    // PURPOSE: To load the .tflite model file
    private fun loadModel(context: Context): ByteBuffer {
        val modelBytes = context.assets.open(MODEL_FILE).use {
            it.readBytes()
        }

        return ByteBuffer.allocateDirect(modelBytes.size).apply {
            order(ByteOrder.nativeOrder())
            put(modelBytes)
            rewind()
        }
    }

    // PURPOSE: Reads the labels from the labels.json file
    private fun loadLabels(context: Context): List<String> {
        val json = context.assets.open(LABELS_FILE).bufferedReader().use {
            it.readText()
        }

        val jsonObject = JSONObject(json)

        return (0 until CLASS_COUNT).map { index ->
            require(jsonObject.has(index.toString())) {
                "Missing label for class index $index."
            }

            jsonObject.getString(index.toString())
        }
    }

    // PURPOSE: For static signs
    @Synchronized
    fun predictStatic(landmarks: FloatArray): Prediction {

        require(landmarks.size == LANDMARK_FEATURE_COUNT) {
            "Expected $LANDMARK_FEATURE_COUNT landmark features."
        }

        val inputSequence = Array(FRAME_COUNT) {
            FloatArray(FEATURE_COUNT)
        }

        for (i in landmarks.indices) {
            inputSequence[0][i] = landmarks[i]
        }

        inputSequence[0][126] = 1f

        return runInference(
            inputSequence,
            STATIC_CLASS_INDICES
        )
    }

    // PURPOSE: For dynamic signs
    @Synchronized
    fun predictDynamic(landmarks: Array<FloatArray>): Prediction {

        require(landmarks.size == FRAME_COUNT) {
            "Expected $FRAME_COUNT frames, but received ${landmarks.size}."
        }

        require(landmarks.all { it.size == FEATURE_COUNT }) {
            "Each frame must contain exactly $FEATURE_COUNT features."
        }

        return runInference(
            landmarks,
            DYNAMIC_CLASS_INDICES
        )
    }

    // PURPOSE: Sends the input to the model and returns the prediction
    private fun runInference(
        landmarks: Array<FloatArray>,
        allowedClassIndices: Set<Int>
    ): Prediction {

        val input = arrayOf(landmarks)
        val output = Array(1) {
            FloatArray(CLASS_COUNT)
        }

        interpreter.run(input, output)

        val probabilities = output[0]

        val predictedIndex = allowedClassIndices.maxByOrNull {
            probabilities[it]
        } ?: error("No valid classes available.")

        return Prediction(
            label = labels[predictedIndex],
            confidence = probabilities[predictedIndex],
            classIndex = predictedIndex
        )
    }

    // PURPOSE: Model interpreter
    override fun close() {
        interpreter.close()
    }
}
