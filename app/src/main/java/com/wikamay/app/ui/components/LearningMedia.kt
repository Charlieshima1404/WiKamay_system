package com.wikamay.app.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
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
import androidx.compose.foundation.clickable
import com.wikamay.app.R

@Composable
fun LearningMedia(
    category: String,
    item: String,
    modifier: Modifier = Modifier,
    label: String? = "Handshape: $item"
) {
    val context = LocalContext.current

    // Normalize names to support apostrophes, spaces, and underscores.
    fun normalizeName(name: String): String {
        return name
            .lowercase()
            .replace(Regex("[^a-z0-9]"), "")
    }

    // Find the matching tutorial media.
    val assetPath = remember(category, item) {
        val folder = "learning/$category"
        val normalizedItem = normalizeName(item)

        context.assets.list(folder)
            ?.firstOrNull { fileName ->
                normalizeName(fileName.substringBeforeLast(".")) ==
                        normalizedItem
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
                val player = remember(videoPath) {
                    ExoPlayer.Builder(context).build().apply {
                        setMediaItem(
                            MediaItem.fromUri(
                                Uri.fromFile(File(videoPath!!))
                            )
                        )

                        repeatMode = Player.REPEAT_MODE_ONE
                        prepare()
                        playWhenReady = true
                    }
                }

                AndroidView(
                    factory = { viewContext ->
                        LayoutInflater.from(viewContext)
                            .inflate(
                                R.layout.learning_video_layout,
                                null,
                                false
                            ) as PlayerView
                    },
                    update = { playerView ->
                        playerView.player = player
                        playerView.isClickable = false
                        playerView.isFocusable = false
                        playerView.isFocusableInTouchMode = false
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember {
                                androidx.compose.foundation.interaction.MutableInteractionSource()
                            },
                            indication = null
                        ) {}
                )

                DisposableEffect(player) {
                    onDispose {
                        player.release()
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