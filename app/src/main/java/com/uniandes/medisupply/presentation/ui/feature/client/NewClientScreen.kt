package com.uniandes.medisupply.presentation.ui.feature.client

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.uniandes.medisupply.R
import com.uniandes.medisupply.presentation.component.TextField
import com.uniandes.medisupply.presentation.ui.theme.MediSupplyTheme
import com.uniandes.medisupply.presentation.ui.theme.spaces
import com.uniandes.medisupply.presentation.viewmodel.NewClientUiState
import com.uniandes.medisupply.presentation.viewmodel.NewClientViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NewClientScreen(
    modifier: Modifier = Modifier,
    viewModel: NewClientViewModel = koinViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    NewClientContent(
        modifier = modifier,
        uiState = uiState.value,
        onUserEvent = { event ->
            viewModel.onEvent(event)
        }
    )
}

@Composable
internal fun NewClientContent(
    modifier: Modifier = Modifier,
    uiState: NewClientUiState,
    onUserEvent: (NewClientViewModel.UserEvent) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {

        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (uiState.showError) {
                AlertDialog(
                    onDismissRequest = {
                        onUserEvent(NewClientViewModel.UserEvent.OnDismissErrorDialog)
                    },
                    title = { Text(stringResource(R.string.error_login)) },
                    text = { Text(uiState.error ?: stringResource(R.string.default_error)) },
                    confirmButton = {
                        Button(onClick = {
                            onUserEvent(NewClientViewModel.UserEvent.OnDismissErrorDialog)
                        }) {
                            Text(stringResource(R.string.ok))
                        }
                    }
                )
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(innerPadding)
                        .align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(MaterialTheme.spaces.medium)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spaces.medium)
                ) {
                    Text(stringResource(R.string.client_data))
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.name,
                        onValueChange = {
                            onUserEvent(NewClientViewModel.UserEvent.OnNameChange(it))
                        },
                        label = { Text(stringResource(R.string.name)) }
                    )
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.type,
                        onValueChange = {
                            onUserEvent(NewClientViewModel.UserEvent.OnTypeChange(it))
                        },
                        label = { Text(stringResource(R.string.type)) }
                    )
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.nit,
                        onValueChange = {
                            onUserEvent(NewClientViewModel.UserEvent.OnNitChange(it))
                        },
                        label = { Text(stringResource(R.string.nit)) }
                    )

                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.country,
                        onValueChange = {
                            onUserEvent(NewClientViewModel.UserEvent.OnCountryChange(it))
                        },
                        label = { Text(stringResource(R.string.country)) }
                    )
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.address,
                        onValueChange = {
                            onUserEvent(NewClientViewModel.UserEvent.OnAddressChange(it))
                        },
                        label = { Text(stringResource(R.string.address)) }
                    )
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.companyEmail,
                        onValueChange = {
                            onUserEvent(NewClientViewModel.UserEvent.OnCompanyEmailChange(it))
                        },
                        label = { Text(stringResource(R.string.email_company)) }
                    )
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.contactName,
                        onValueChange = {
                            onUserEvent(NewClientViewModel.UserEvent.OnContactNameChange(it))
                        },
                        label = { Text(stringResource(R.string.contact_name)) }
                    )
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value =  uiState.position,
                        onValueChange = {
                            onUserEvent(NewClientViewModel.UserEvent.OnPositionChange(it))
                        },
                        label = { Text(stringResource(R.string.position)) }
                    )
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.contactPhone,
                        onValueChange = {
                            onUserEvent(NewClientViewModel.UserEvent.OnContactPhoneChange(it))
                        },
                        label = { Text(stringResource(R.string.phone_number)) }
                    )
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.contactEmail    ,
                        onValueChange = {
                            onUserEvent(NewClientViewModel.UserEvent.OnContactEmailChange(it))
                        },
                        label = { Text(stringResource(R.string.email_contact)) }
                    )

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onUserEvent(NewClientViewModel.UserEvent.OnSaveClientClick)
                        }
                    ) {
                        Text(stringResource(R.string.register_client))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewClientScreenPreview() {
    MediSupplyTheme {
        NewClientContent(
            uiState = NewClientUiState(),
            onUserEvent = {}
        )
    }
}