package com.wikamay.app.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.util.concurrent.Executors

@SuppressLint("MissingPermission")
@Composable
fun CameraXPreview(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    lensFacing: Int = CameraSelector.LENS_FACING_FRONT,
    label: String? = null,
    onFrame: ((ImageProxy) -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var cameraError by remember {
        mutableStateOf<String?>(null)
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->
            hasCameraPermission = granted
        }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val previewView = remember(context) {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode =
                PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    val analysisExecutor = remember {
        Executors.newSingleThreadExecutor()
    }

    // PURPOSE: Remember the latest onFrame lambda that's used for image analysis
    val currentOnFrame by rememberUpdatedState(onFrame)

    DisposableEffect(analysisExecutor) {
        onDispose {
            analysisExecutor.shutdown()
        }
    }

    DisposableEffect(
        lifecycleOwner,
        previewView,
        lensFacing,
        hasCameraPermission,
        onFrame != null
    ) {
        var cameraProvider: ProcessCameraProvider? = null
        var disposed = false

        cameraError = null

        if (hasCameraPermission) {
            val cameraProviderFuture =
                ProcessCameraProvider.getInstance(context)

            val executor = ContextCompat.getMainExecutor(context)

            cameraProviderFuture.addListener({
                if (!disposed) {
                    try {
                        val provider = cameraProviderFuture.get()
                        cameraProvider = provider

                        val selector =
                            CameraSelector.Builder()
                                .requireLensFacing(lensFacing)
                                .build()

                        provider.unbindAll()

                        if (provider.hasCamera(selector)) {

                            val preview = Preview.Builder()
                                .build()

                            preview.setSurfaceProvider(
                                previewView.surfaceProvider
                            )

                            if (onFrame != null) {

                                val imageAnalysis =
                                    ImageAnalysis.Builder()
                                        .setBackpressureStrategy(
                                            ImageAnalysis
                                                .STRATEGY_KEEP_ONLY_LATEST
                                        )
                                        .build()

                                imageAnalysis.setAnalyzer(
                                    analysisExecutor
                                ) { imageProxy ->

                                    try {
                                        currentOnFrame?.invoke(imageProxy)
                                    } catch (exception: Exception) {
                                        Log.e(
                                            "CameraXPreview",
                                            "Frame analysis failed",
                                            exception
                                        )
                                    } finally {
                                        imageProxy.close()
                                    }
                                }

                                provider.bindToLifecycle(
                                    lifecycleOwner,
                                    selector,
                                    preview,
                                    imageAnalysis
                                )

                            } else {
                                provider.bindToLifecycle(
                                    lifecycleOwner,
                                    selector,
                                    preview
                                )
                            }

                            cameraError = null

                        } else {
                            cameraError =
                                if (
                                    lensFacing ==
                                    CameraSelector.LENS_FACING_FRONT
                                ) {
                                    "Front camera is not available. Check your emulator camera settings."
                                } else {
                                    "Back camera is not available. Check your emulator camera settings."
                                }
                        }

                    } catch (exception: Exception) {
                        cameraError =
                            "Camera initialization failed: " +
                                    (
                                            exception.localizedMessage
                                                ?: "Unknown camera error"
                                            )

                        exception.printStackTrace()
                    }
                }
            }, executor)
        }

        onDispose {
            disposed = true
            cameraProvider?.unbindAll()
        }
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(Color.Black)
    ) {

        if (hasCameraPermission) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )
        }

        content()

        if (!hasCameraPermission) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Camera permission is required",
                        color = Color.White,
                        fontSize = 14.sp
                    )

                    TextButton(
                        onClick = {
                            permissionLauncher.launch(
                                Manifest.permission.CAMERA
                            )
                        }
                    ) {
                        Text("Grant Camera Permission")
                    }
                }
            }
        }

        cameraError?.let { error ->
            Text(
                text = error,
                color = Color.White,
                fontSize = 13.sp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(
                        Color.Black.copy(alpha = 0.75f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            )
        }

        if (label != null) {
            Text(
                text = label,
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .background(
                        Color.Black.copy(alpha = 0.5f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            )
        }
    }
}


// PURPOSE: Placeholder for the camera preview
@Composable
fun CameraPlaceholder(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    lensFacing: Int = CameraSelector.LENS_FACING_FRONT,
    label: String? = null,
    onFrame: ((ImageProxy) -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit = {}
) {
    CameraXPreview(
        modifier = modifier,
        shape = shape,
        lensFacing = lensFacing,
        label = label,
        onFrame = onFrame,
        content = content
    )
}
