package com.wikamay.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.wikamay.app.navigation.WiKamayNavGraph
import com.wikamay.app.ui.theme.WiKamayTheme
import com.wikamay.app.ml.Recognition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.wikamay.app.ml.HandLandmark

/**
 * PURPOSE: The only Activity of the app. Android starts here, and it simply hosts
 * the Compose UI (theme + navigation graph). You rarely need to edit this file.
 */
class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "WiKamayModelTest"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // FUNCTION: Lets the app draw behind the status/navigation bars (modern full-screen look).
        enableEdgeToEdge()
        setContent {
            // PURPOSE: Applies our colors and typography to every screen.
            WiKamayTheme {
                // NAVIGATION: Starts the navigation graph, which begins at the Splash screen.
                WiKamayNavGraph()
            }
        }
    }
}
