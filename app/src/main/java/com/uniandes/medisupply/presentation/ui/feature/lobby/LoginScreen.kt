package com.uniandes.medisupply.presentation.ui.feature.lobby

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uniandes.medisupply.R
import com.uniandes.medisupply.presentation.component.SecureTextField
import com.uniandes.medisupply.presentation.component.TextField
import com.uniandes.medisupply.presentation.model.LoginUiState
import com.uniandes.medisupply.presentation.ui.theme.MediSupplyTheme
import com.uniandes.medisupply.presentation.ui.theme.spaces
import com.uniandes.medisupply.presentation.viewmodel.LoginViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    LoginContent(
        uiState = uiState,
        modifier = modifier,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun LoginContent(
    uiState: LoginUiState,
    modifier: Modifier = Modifier,
    onEvent: (LoginViewModel.UserEvent) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.spaces.medium),
        contentAlignment = Alignment.Center
    ) {
        if (uiState.showHiddenDialog) {
            AlertDialog(
                onDismissRequest = {
                    onEvent(LoginViewModel.UserEvent.OnDismissErrorDialog)
                },
                title = { Text(stringResource(R.string.error_login)) },
                text = {
                    TextField(
                        value = uiState.baseUrl,
                        onValueChange = {
                            onEvent(LoginViewModel.UserEvent.OnBaseUrlChange(it))
                        },
                        label = { Text("BASE URL") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        onEvent(LoginViewModel.UserEvent.OnSaveBaseUrl)
                    }) {
                        Text(stringResource(R.string.ok))
                    }
                }
            )
        }
        if (uiState.showError) {
            AlertDialog(
                onDismissRequest = {
                    onEvent(LoginViewModel.UserEvent.OnDismissErrorDialog)
                },
                title = { Text(stringResource(R.string.error_login)) },
                text = { Text(uiState.error ?: stringResource(R.string.default_error)) },
                confirmButton = {
                    Button(onClick = {
                        onEvent(LoginViewModel.UserEvent.OnDismissErrorDialog)
                    }) {
                        Text(stringResource(R.string.ok))
                    }
                }
            )
        }

        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = stringResource(R.string.login))
                TextField(
                    value = uiState.email,
                    onValueChange = { onEvent(LoginViewModel.UserEvent.OnEmailChange(it)) },
                    label = {
                        Text(stringResource(R.string.email))
                    },
                    isError = uiState.emailError != null,
                    supportingText = uiState.emailError,
                    modifier = Modifier.fillMaxWidth()
                )
                SecureTextField(
                    value = uiState.password,
                    onValueChange = { onEvent(LoginViewModel.UserEvent.OnPasswordChange(it)) },
                    label = {
                        Text(stringResource(R.string.password))
                    },
                    isError = uiState.passwordError != null,
                    supportingText = uiState.passwordError,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { onEvent(LoginViewModel.UserEvent.OnPrimaryButtonClick) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.loginButtonEnable,

                ) {
                    Text(stringResource(R.string.login))
                }
                Text(
                    modifier = Modifier.clickable {
                        onEvent(LoginViewModel.UserEvent.OnHiddenAccess)
                    },
                    text = "Test-app", )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExampleScreenPreview() {
    val uiState = LoginUiState(
        isLoading = false,
        email = "",
        password = ""
    )
    MediSupplyTheme {
        LoginContent(
            uiState = uiState,
            modifier = Modifier
        )
    }
}
