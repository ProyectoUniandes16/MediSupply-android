package com.uniandes.medisupply.presentation.ui.feature.client

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.uniandes.medisupply.R
import com.uniandes.medisupply.common.ExcludeFromJacocoGeneratedReport
import com.uniandes.medisupply.presentation.component.SpinnerDropdown
import com.uniandes.medisupply.presentation.component.TextField
import com.uniandes.medisupply.presentation.component.TopAppBar
import com.uniandes.medisupply.presentation.ui.theme.MediSupplyTheme
import com.uniandes.medisupply.presentation.ui.theme.spaces
import com.uniandes.medisupply.presentation.viewmodel.NewClientUiState
import com.uniandes.medisupply.presentation.viewmodel.NewClientViewModel
import org.koin.compose.viewmodel.koinViewModel

@ExcludeFromJacocoGeneratedReport
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NewClientContent(
    modifier: Modifier = Modifier,
    uiState: NewClientUiState,
    onUserEvent: (NewClientViewModel.UserEvent) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.new_client),
                navigationIcon = {
                    IconButton(onClick = {
                        onUserEvent(NewClientViewModel.UserEvent.OnBackClick)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.showError) {
                AlertDialog(
                    onDismissRequest = {
                        onUserEvent(NewClientViewModel.UserEvent.OnDismissErrorDialog)
                    },
                    title = { Text(stringResource(R.string.error_client)) },
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
                        .testTag("LOADING")
                        .align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = MaterialTheme.spaces.medium)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spaces.medium)
                ) {
                    Spacer(modifier.height(MaterialTheme.spaces.medium))
                    Text(stringResource(R.string.client_data))
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.name,
                        onValueChange = {
                            onUserEvent(NewClientViewModel.UserEvent.OnNameChange(it))
                        },
                        isError = uiState.errorName != null,
                        supportingText = uiState.errorName,
                        label = { Text(stringResource(R.string.company_name)) }
                    )
                    SpinnerDropdown(
                        options = uiState.clientTypes.values.toList(),
                        label = stringResource(R.string.type),
                        onOptionSelected = {
                            onUserEvent(NewClientViewModel.UserEvent.OnTypeChange(it))
                        }
                    )
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.nit,
                        onValueChange = {
                            onUserEvent(NewClientViewModel.UserEvent.OnNitChange(it))
                        },
                        label = { Text(stringResource(R.string.nit)) },
                        isError = uiState.errorNit != null,
                        supportingText = uiState.errorNit
                    )

                    SpinnerDropdown(
                        options = uiState.countryList,
                        label = stringResource(R.string.country),
                        onOptionSelected = {
                            onUserEvent(NewClientViewModel.UserEvent.OnCountryChange(it))
                        }
                    )
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.address,
                        onValueChange = {
                            onUserEvent(NewClientViewModel.UserEvent.OnAddressChange(it))
                        },
                        label = { Text(stringResource(R.string.address)) },
                        isError = uiState.errorAddress != null,
                        supportingText = uiState.errorAddress
                    )
                    if (uiState.showCompanyEmailField) {
                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = uiState.companyEmail,
                            onValueChange = {
                                onUserEvent(NewClientViewModel.UserEvent.OnCompanyEmailChange(it))
                            },
                            label = { Text(stringResource(R.string.email_company)) },
                            isError = uiState.errorCompanyEmail != null,
                            supportingText = uiState.errorCompanyEmail
                        )
                    }
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.contactName,
                        onValueChange = {
                            onUserEvent(NewClientViewModel.UserEvent.OnContactNameChange(it))
                        },
                        label = { Text(stringResource(R.string.contact_name)) },
                        isError = uiState.errorContactName != null,
                        supportingText = uiState.errorContactName
                    )
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.position,
                        onValueChange = {
                            onUserEvent(NewClientViewModel.UserEvent.OnPositionChange(it))
                        },
                        label = { Text(stringResource(R.string.position)) },
                        isError = uiState.errorPosition != null,
                        supportingText = uiState.errorPosition
                    )
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.contactPhone,
                        onValueChange = {
                            onUserEvent(NewClientViewModel.UserEvent.OnContactPhoneChange(it))
                        },
                        label = { Text(stringResource(R.string.phone_number)) },
                        isError = uiState.errorContactPhone != null,
                        supportingText = uiState.errorContactPhone
                    )
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.contactEmail,
                        onValueChange = {
                            onUserEvent(NewClientViewModel.UserEvent.OnContactEmailChange(it))
                        },
                        label = { Text(stringResource(R.string.email_contact)) },
                        isError = uiState.errorContactEmail != null,
                        supportingText = uiState.errorContactEmail
                    )

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onUserEvent(NewClientViewModel.UserEvent.OnSaveClientClick)
                        },
                        enabled = uiState.primaryButtonEnabled
                    ) {
                        Text(stringResource(R.string.register_client))
                    }
                    Spacer(modifier = Modifier.height(MaterialTheme.spaces.medium))
                }
            }
        }
    }
}

@ExcludeFromJacocoGeneratedReport
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
