package com.uniandes.medisupply.presentation.ui.feature.order

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.uniandes.medisupply.R
import com.uniandes.medisupply.domain.model.Client
import com.uniandes.medisupply.presentation.component.AlertDialog
import com.uniandes.medisupply.presentation.component.AvatarText
import com.uniandes.medisupply.presentation.component.BackNavigation
import com.uniandes.medisupply.presentation.component.TopAppBar
import com.uniandes.medisupply.presentation.ui.theme.spaces
import com.uniandes.medisupply.presentation.viewmodel.order.ClientsOrderState
import com.uniandes.medisupply.presentation.viewmodel.order.ClientListOrderViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClientListOrderScreen(
    modifier: Modifier = Modifier,
    viewModel: ClientListOrderViewModel = koinViewModel()
) {
    val uiState = viewModel.clientsUiState.collectAsState()
    ClientOrderListContent(
        modifier = modifier,
        uiState = uiState.value,
        onEvent = { event ->
            viewModel.onEvent(event)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClientOrderListContent(
    modifier: Modifier = Modifier,
    uiState: ClientsOrderState,
    onEvent: (ClientListOrderViewModel.UserEvent) -> Unit
) {
    if (uiState.showError) {
        AlertDialog(
            title = stringResource(R.string.default_error),
            message = uiState.error ?: stringResource(R.string.default_error),
            onConfirm = {
                onEvent(ClientListOrderViewModel.UserEvent.OnErrorDialogDismissed)
            },
            onDismissRequest = {
                onEvent(ClientListOrderViewModel.UserEvent.OnErrorDialogDismissed)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = BackNavigation {
                    onEvent(ClientListOrderViewModel.UserEvent.OnBackClicked)
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier.padding(innerPadding).fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                if (uiState.clients.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_clients_found)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .padding(MaterialTheme.spaces.medium)
                            .fillMaxSize()
                    ) {
                        item {
                            Text(
                                stringResource(R.string.select_client),
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                        items(uiState.clients) {
                            ClientOrderListItem(
                                client = it,
                                onClick = { client ->
                                    onEvent(ClientListOrderViewModel.UserEvent.OnClientClicked(client))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientOrderListItem(
    modifier: Modifier = Modifier,
    client: Client,
    onClick: (client: Client) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spaces.small)
    ) {
        Row(
            modifier = Modifier.clickable {
                onClick(client)
            }.padding(
                horizontal = MaterialTheme.spaces.medium,
                vertical = MaterialTheme.spaces.large
            ).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spaces.medium)
        ) {
            AvatarText(client.name)
            Text(
                text = client.name,
            )
        }
    }
}

@Composable
@Preview
fun ClientOrderListScreenPreview() {
    ClientOrderListContent(
        uiState = ClientsOrderState(),
        onEvent = {}
    )
}
