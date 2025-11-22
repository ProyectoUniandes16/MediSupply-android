package com.uniandes.medisupply.presentation.ui.feature.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.uniandes.medisupply.R
import com.uniandes.medisupply.presentation.ui.theme.MediSupplyTheme
import com.uniandes.medisupply.presentation.ui.theme.spaces
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.uniandes.medisupply.domain.model.Client
import com.uniandes.medisupply.presentation.viewmodel.ClientListUiState
import com.uniandes.medisupply.presentation.viewmodel.ClientListViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClientListScreen(
    modifier: Modifier = Modifier,
    viewModel: ClientListViewModel = koinViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                Log.i("ClientListScreen", "ON_RESUME - Refreshing clients")
                viewModel.onEvent(ClientListViewModel.UserEvent.OnRefreshClients)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    ClientDetailContent(
        modifier = modifier,
        onUserEvent = { event ->
            viewModel.onEvent(event)
        },
        uiState = uiState.value
    )
}

@Composable
internal fun ClientDetailContent(
    modifier: Modifier = Modifier,
    uiState: ClientListUiState,
    onUserEvent: (ClientListViewModel.UserEvent) -> Unit = {}
) {
    Scaffold(
        topBar = {
        }
    ) { innerPadding ->

        if (uiState.showError) {
            AlertDialog(
                onDismissRequest = {
                    onUserEvent(ClientListViewModel.UserEvent.OnDismissErrorDialog)
                },
                title = { Text(stringResource(R.string.error_client)) },
                text = { Text(uiState.error ?: stringResource(R.string.default_error)) },
                confirmButton = {
                    Button(onClick = {
                        onUserEvent(ClientListViewModel.UserEvent.OnDismissErrorDialog)
                    }) {
                        Text(stringResource(R.string.ok))
                    }
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.BottomEnd
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.clients.isEmpty()) {
                        Text(stringResource(R.string.no_clients_found))
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = MaterialTheme.spaces.medium),
                            contentPadding = PaddingValues(
                                top = MaterialTheme.spaces.medium,
                                bottom = MaterialTheme.spaces.medium +
                                        MaterialTheme.spaces.xLarge +
                                        MaterialTheme.spaces.xLarge +
                                        MaterialTheme.spaces.small
                            ),
                            verticalArrangement = Arrangement.spacedBy(
                                MaterialTheme.spaces.medium
                            )
                        ) {
                            items(uiState.clients) {
                                ClientCard(
                                    client = it,
                                    onOrderClicked = { client ->
                                        onUserEvent(
                                            ClientListViewModel.UserEvent.OnClientOrderClicked(
                                                client
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
                FloatingActionButton(
                    modifier = Modifier
                        .padding(MaterialTheme.spaces.medium),
                    onClick = {
                        onUserEvent(ClientListViewModel.UserEvent.OnNewClientClick)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = MaterialTheme.spaces.medium,
                            vertical = MaterialTheme.spaces.small
                        )
                    ) {
                        Text(stringResource(R.string.new_client))
                    }
                }
            }
        }
    }
}

@Composable
fun ClientCard(
    modifier: Modifier = Modifier,
    client: Client,
    onOrderClicked: (client: Client) -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(
            MaterialTheme.spaces.small
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = MaterialTheme.spaces.medium
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spaces.medium)
        ) {
            Text(
                text = client.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = MaterialTheme.spaces.small)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = MaterialTheme.spaces.small)
            ) {
                Icon(
                    Icons.Default.Add, contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.spaces.medium),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(MaterialTheme.spaces.small))
                Text(client.name, fontSize = 14.sp, color = Color.DarkGray)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = MaterialTheme.spaces.small)
            ) {
                Icon(
                    Icons.Default.Phone,
                    contentDescription = stringResource(R.string.phone_number),
                    modifier = Modifier.size(MaterialTheme.spaces.medium),
                    tint = Color.Gray
                )
                Spacer(
                    modifier = Modifier.width(
                        MaterialTheme.spaces.small
                    )
                )
                Text(
                    client.contactInfo.phone,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = MaterialTheme.spaces.medium)
            ) {
                Icon(
                    Icons.Default.LocationOn, contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.spaces.medium),
                    tint = Color.Gray
                )
                Spacer(
                    modifier = Modifier.width(
                        MaterialTheme.spaces.small
                    )
                )
                Text(
                    client.address,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }

            HorizontalDivider(color = Color.LightGray)

            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = MaterialTheme.spaces.medium),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        onOrderClicked(client)
                    },
                    shape = RoundedCornerShape(MaterialTheme.spaces.small),
                    contentPadding = PaddingValues(
                        horizontal = MaterialTheme.spaces.large,
                        vertical = MaterialTheme.spaces.small
                    )
                ) {
                    Text(
                        stringResource(R.string.new_order),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun ClientDetailContentPreview() {
    MediSupplyTheme {
        ClientDetailContent(
            onUserEvent = {},
            uiState = ClientListUiState(
                isLoading = false,
                clients = listOf(
                    Client(
                        id = 1,
                        name = "PharmaCorp",
                        address = "123 Health St, Medicity",
                        email = "alla@medisupply.com",
                        contactInfo = com.uniandes.medisupply.domain.model.ClientContactInfo(
                            name = "Alice Johnson",
                            phone = "+1 555-1234",
                            email = "alice@medisupply.com",
                            position = "Sales Manager"
                        )
                    )
                )
            )
        )
    }
}
