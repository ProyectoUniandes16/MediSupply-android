package com.uniandes.medisupply.presentation.component

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.uniandes.medisupply.R

@Composable
fun AlertDialog(
    title: String,
    message: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    confirmButtonText: String = stringResource(R.string.retry),
    dismissButtonText: String = stringResource(R.string.cancel)
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            Button(
                colors = ButtonDefaults.textButtonColors(),
                onClick = onConfirm
            ) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            Button(
                colors = ButtonDefaults.textButtonColors(),
                onClick = onDismissRequest
            ) {
                Text(dismissButtonText)
            }
        },
    )
}