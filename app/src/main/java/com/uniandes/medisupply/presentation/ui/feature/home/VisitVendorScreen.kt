package com.uniandes.medisupply.presentation.ui.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.uniandes.medisupply.R
import com.uniandes.medisupply.common.ExcludeFromJacocoGeneratedReport
import com.uniandes.medisupply.presentation.component.AlertDialog
import com.uniandes.medisupply.presentation.component.AvatarText
import com.uniandes.medisupply.presentation.component.Card
import com.uniandes.medisupply.presentation.model.VisitStatusUI
import com.uniandes.medisupply.presentation.model.VisitUI
import com.uniandes.medisupply.presentation.ui.theme.spaces
import com.uniandes.medisupply.presentation.viewmodel.vendor.VisitListViewmodel
import com.uniandes.medisupply.presentation.viewmodel.vendor.VisitUiState
import org.koin.compose.viewmodel.koinViewModel

@ExcludeFromJacocoGeneratedReport
@Composable
fun VisitVendorScreen(
    modifier: Modifier = Modifier,
    viewModel: VisitListViewmodel = koinViewModel(),
) {
    val uiState = viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.onEvent(VisitListViewmodel.UserEvent.OnScreenLoaded)
        }
    }
    VisitVendorContent(
        uiState = uiState.value,
        modifier = modifier,
        onEvent = viewModel::onEvent
    )
}

@Composable
internal fun VisitVendorContent(
    modifier: Modifier = Modifier,
    uiState: VisitUiState,
    onEvent: (VisitListViewmodel.UserEvent) -> Unit
) {
    if (uiState.showError) {
        AlertDialog(
            message = uiState.errorMessage ?: stringResource(R.string.default_error),
            onDismissRequest = {
                onEvent(VisitListViewmodel.UserEvent.OnErrorDialogDismissed)
            },
            dismissButtonText = stringResource(R.string.retry),
            onConfirm = {}
        )
    }
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.testTag("LOADING_INDICATOR")
            )
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = MaterialTheme.spaces.medium,
                            vertical = MaterialTheme.spaces.small
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = {
                            onEvent(VisitListViewmodel.UserEvent.OnBackwardDateClicked)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                    Text(
                        uiState.selectedDate
                    )
                    IconButton(
                        onClick = {
                            onEvent(VisitListViewmodel.UserEvent.OnForwardDateClicked)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                            contentDescription = stringResource(R.string.forwrard)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.visitList.isEmpty()) {
                        Text(text = stringResource(R.string.no_visits_today))
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            itemsIndexed(uiState.visitList) { index, visit ->
                                VisitCard(
                                    visit = visit,
                                    position = index + 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VisitCard(
    modifier: Modifier = Modifier,
    visit: VisitUI,
    position: Int
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spaces.medium, vertical = MaterialTheme.spaces.small)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spaces.medium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarText(
                    position.toString(),
                    color = when (visit.status) {
                        VisitStatusUI.COMPLETED -> MaterialTheme.colorScheme.primaryContainer
                        VisitStatusUI.IN_PROGRESS -> MaterialTheme.colorScheme.tertiary
                        VisitStatusUI.PENDING -> MaterialTheme.colorScheme.secondaryContainer
                    }
                )
                Spacer(Modifier.width(MaterialTheme.spaces.small))
                Column {
                    Text(
                        text = visit.clientName,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.Black)
                Spacer(Modifier.width(4.dp))
                Text(visit.contactName, modifier = Modifier.padding(top = 4.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, contentDescription = null, tint = Color.Black)
                Spacer(Modifier.width(4.dp))
                Text(visit.clientAddress)
            }
            Spacer(Modifier.height(8.dp))
            HorizontalDivider()

            Box(
                modifier = Modifier.padding(
                    vertical = MaterialTheme.spaces.small
                )
            ) {
                when (visit.status) {
                    VisitStatusUI.COMPLETED -> {
                        Text(
                            text = "VISITA EXITOSAAS_DSADASDSA",
                            color = Color.Green,
                            fontSize = 12.sp
                        )
                    }
                    VisitStatusUI.IN_PROGRESS -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = stringResource(visit.status.resId),
                                color = MaterialTheme.colorScheme.tertiary,
                                fontSize = 12.sp
                            )
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = { /* acción */ }) {
                                Text(stringResource(R.string.visit_end))
                            }
                        }
                    }
                    VisitStatusUI.PENDING -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                modifier = Modifier.weight(1f),
                                onClick = {}
                            ) {
                                Text(stringResource(R.string.visit_start))
                            }
                            IconButton(
                                onClick = {}
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = stringResource(R.string.location)
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@ExcludeFromJacocoGeneratedReport
@Composable
@Preview(showBackground = true)
private fun PreviewVisitCard() {
    VisitVendorContent(
        uiState = VisitUiState(
            selectedDate = "2025-11-20",
            visitList = List(10) {
                VisitUI(
                    status = when(it % 3) {
                        0 -> VisitStatusUI.COMPLETED
                        1 -> VisitStatusUI.PENDING
                        else -> VisitStatusUI.IN_PROGRESS
                    },
                    visitDate = "2025-11-21",
                    clientName = "Health Corp",
                    clientAddress = "123 Main St, City",
                    contactName = "John Doe",
                )
            }
        ),
        onEvent = {}
    )
}