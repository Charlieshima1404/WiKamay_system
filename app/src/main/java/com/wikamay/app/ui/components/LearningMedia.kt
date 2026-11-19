package com.wikamay.app.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.VideoView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@Composable
fun LearningMedia(
    category: String,
    item: String,
    modifier: Modifier = Modifier,
    label: String? = "Handshape: $item"
) {
    val context = LocalContext.current

    // Find the file matching the selected letter or number.
    val assetPath = remember(category, item) {
        val folder = "learning/$category"

        context.assets.list(folder)
            ?.firstOrNull { fileName ->
                fileName.substringBeforeLast(".")
                    .equals(item, ignoreCase = true)
            }
            ?.let { fileName -> "$folder/$fileName" }
    }

    var imageBitmap by remember(assetPath) {
        mutableStateOf<Bitmap?>(null)
    }

    var videoPath by remember(assetPath) {
        mutableStateOf<String?>(null)
    }

    // Load images or copy videos from assets into the app cache.
    LaunchedEffect(assetPath) {
        imageBitmap = null
        videoPath = null

        if (assetPath != null) {
            val result = withContext(Dispatchers.IO) {
                val extension = assetPath.substringAfterLast(".")
                    .lowercase()

                when (extension) {
                    "png", "jpg", "jpeg", "webp" -> {
                        val bitmap = context.assets.open(assetPath).use {
                            BitmapFactory.decodeStream(it)
                        }
                        bitmap to null
                    }

                    "mp4", "mov", "3gp" -> {
                        val cacheFile = File(
                            context.cacheDir,
                            "learning_${category}_${assetPath.substringAfterLast("/")}"
                        )

                        if (!cacheFile.exists() || cacheFile.length() == 0L) {
                            context.assets.open(assetPath).use { input ->
                                FileOutputStream(cacheFile).use { output ->
                                    input.copyTo(output)
                                }
                            }
                        }

                        null to cacheFile.absolutePath
                    }

                    else -> null to null
                }
            }

            imageBitmap = result.first
            videoPath = result.second
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.DarkGray)
    ) {
        when {
            imageBitmap != null -> {
                Image(
                    bitmap = imageBitmap!!.asImageBitmap(),
                    contentDescription = "Handshape for $item",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            videoPath != null -> {
                val videoView = remember(videoPath) {
                    VideoView(context).apply {
                        setVideoPath(videoPath!!)
                        setOnPreparedListener { player ->
                            player.isLooping = true
                            player.start()
                        }
                    }
                }

                AndroidView(
                    factory = { videoView },
                    modifier = Modifier.fillMaxSize()
                )

                DisposableEffect(videoView) {
                    onDispose {
                        videoView.stopPlayback()
                    }
                }
            }

            else -> {
                Text(
                    text = if (assetPath == null) {
                        "No media found"
                    } else {
                        "Loading..."
                    },
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        // Keep the original handshape label.
        if (label != null) {
            Text(
                text = label,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            )
        }
    }
}