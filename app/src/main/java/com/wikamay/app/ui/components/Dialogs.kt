package com.wikamay.app.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

/**
 * PURPOSE: A simple pop-up that tells the user a feature is not implemented yet.
 * PLACEHOLDER: Every button that will need AI/AR/speech/camera later shows this dialog for now.
 * FUNCTION: Calls [onDismiss] when the user taps OK or taps outside the dialog.
 */
@Composable
fun PlaceholderDialog(title: String, message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text("OK") } },
        title = { Text(title) },
        text = { Text(message) }
    )
}
