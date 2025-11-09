package com.uniandes.medisupply.presentation.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.uniandes.medisupply.R

@Composable
fun AlertDialog(
    title: String = stringResource(R.string.default_error_title),
    message: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    confirmButtonText: String? = stringResource(R.string.retry),
    dismissButtonText: String = stringResource(R.string.cancel)
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            if (confirmButtonText != null) {
                Button(
                    colors = ButtonDefaults.textButtonColors(),
                    onClick = onConfirm
                ) {
                    Text(confirmButtonText)
                }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingAlertDialog(
    message: String = stringResource(R.string.loading),
    onDismissRequest: () -> Unit = {},
) =  BasicAlertDialog(
    onDismissRequest = onDismissRequest,
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row {
            CircularProgressIndicator()
            Text(message)
        }
    }
}
