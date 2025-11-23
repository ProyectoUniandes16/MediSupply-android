package com.uniandes.medisupply.presentation.ui.feature.product

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uniandes.medisupply.R
import com.uniandes.medisupply.common.formatCurrency
import com.uniandes.medisupply.presentation.component.AlertDialog
import com.uniandes.medisupply.presentation.component.BackNavigation
import com.uniandes.medisupply.presentation.component.Card
import com.uniandes.medisupply.presentation.component.LoadingAlertDialog
import com.uniandes.medisupply.presentation.component.TextField
import com.uniandes.medisupply.presentation.component.TopAppBar
import com.uniandes.medisupply.presentation.model.ProductUI
import com.uniandes.medisupply.presentation.model.StockStatusUI
import com.uniandes.medisupply.presentation.ui.theme.spaces
import com.uniandes.medisupply.presentation.viewmodel.product.ProductDetailState
import com.uniandes.medisupply.presentation.viewmodel.product.ProductDetailViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProductDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: ProductDetailViewModel = koinViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    ProductDetailContent(
        modifier = modifier,
        onUserEvent = viewModel::onEvent,
        uiState = uiState.value
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailContent(
    modifier: Modifier = Modifier,
    onUserEvent: (ProductDetailViewModel.UserEvent) -> Unit,
    uiState: ProductDetailState
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val snackbarMessage = stringResource(R.string.video_upload_success)
    LaunchedEffect(uiState.showSuccessMessage) {
       if (uiState.showSuccessMessage) {
           scope.launch {
               snackbarHostState.showSnackbar(snackbarMessage)
                onUserEvent(ProductDetailViewModel.UserEvent.OnDismissSuccessMessage)
           }
       }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = "",
                navigationIcon = BackNavigation {
                    onUserEvent(ProductDetailViewModel.UserEvent.OnBackClicked)
                }
            )
        },
        content = { paddingValues ->
            if (uiState.showVideoUploadDialog) {
                VideoAlertDialog(
                    videoName = uiState.videoFileName ?: "",
                    videoRecommendation = uiState.description ?: "",
                    onRecommendationChanged = { description ->
                        onUserEvent(ProductDetailViewModel.UserEvent.OnDescriptionChanged(description))
                    },
                    onDismissRequest = {
                        onUserEvent(ProductDetailViewModel.UserEvent.OnVideoUploadCanceled)
                    },
                    onConfirm = {
                        onUserEvent(ProductDetailViewModel.UserEvent.OnVideoUploadConfirmed)
                    }
                )
            }
            if (uiState.isUploading) {
                LoadingAlertDialog()
            }
            if (uiState.showError) {
                AlertDialog(
                    title = stringResource(R.string.default_error_title),
                    message = uiState.error ?: stringResource(R.string.default_error_message),
                    onDismissRequest = {
                        onUserEvent(ProductDetailViewModel.UserEvent.OnDismissError)
                    },
                    confirmButtonText = stringResource(R.string.ok),
                    onConfirm = { onUserEvent(ProductDetailViewModel.UserEvent.OnDismissError) }
                )
            }
            ProductDetailView(
                modifier = Modifier.padding(paddingValues),
                uiState = uiState
            )
        },
        bottomBar = {
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                if (uri != null) {
                    onUserEvent(ProductDetailViewModel.UserEvent.OnVideoSelected(uri))
                }
            }
            Button(
                modifier = Modifier
                    .padding(MaterialTheme.spaces.medium)
                    .fillMaxWidth(),
                onClick = {
                    launcher.launch("video/*")
                }
            ) {
                Text(text = stringResource(R.string.add_video))
            }
        }
    )
}

@Composable
fun ProductDetailView(
    modifier: Modifier = Modifier,
    uiState: ProductDetailState
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(MaterialTheme.spaces.medium)
    ) {
        // Product Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spaces.medium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = uiState.product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                AssistChip(
                    onClick = {
                    },
                    label = {
                        Text(uiState.product.category)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description and Price Card
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spaces.medium)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.price),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = uiState.product.price.formatCurrency("USD"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = stringResource(R.string.stock),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${uiState.product.totalStock}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(MaterialTheme.spaces.medium))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(MaterialTheme.spaces.medium))

                // Lot Information
                InfoRow(label = stringResource(R.string.batch), value = uiState.product.batchNumber)
                Spacer(modifier = Modifier.height(MaterialTheme.spaces.small))
                InfoRow(label = stringResource(R.string.expiration_date), value = uiState.product.expirationDate)
            }
        }

        Spacer(modifier = Modifier.height(MaterialTheme.spaces.medium))

        Box {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(MaterialTheme.spaces.medium)
                    ) {
                        Text(
                            text = stringResource(R.string.stock_details),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (uiState.product.stock.isEmpty()) {
                            Text(
                                text = stringResource(R.string.out_stock),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = MaterialTheme.spaces.small)
                            )
                        } else {
                            uiState.product.stock.forEach {
                                Spacer(modifier = Modifier.height(MaterialTheme.spaces.medium))
                                InfoRow(
                                    label = it.location,
                                    value = it.quantity.toString()
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(MaterialTheme.spaces.medium))
                        AssistChip(
                            onClick = { },
                            label = {
                                Text(stringResource(uiState.product.stockStatus.resId))
                            }
                        )
                    }
                }
            }
        }

        /*Spacer(modifier = Modifier.height(MaterialTheme.spaces.medium))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spaces.medium)
            ) {
                Text(
                    text = "Videos de Recomendación",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                VideoItem(title = "Como usar producto")
                Spacer(modifier = Modifier.height(8.dp))
                VideoItem(title = "Limpiar producto")
            }
        }*/
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoAlertDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    videoName: String,
    videoRecommendation: String,
    onRecommendationChanged: (String) -> Unit,
    onConfirm: () -> Unit
) {
    BasicAlertDialog(
        content = {
            Column(
                modifier = modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxWidth()
                    .padding(MaterialTheme.spaces.medium),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spaces.medium)
            ) {
                Text(
                    stringResource(R.string.upload_video),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(stringResource(R.string.confirm_upload_video, videoName))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = videoRecommendation,
                    onValueChange = onRecommendationChanged,
                    label = { Text(stringResource(R.string.add_recommendation)) }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = MaterialTheme.spaces.medium),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        colors = ButtonDefaults.textButtonColors(),
                        onClick = onDismissRequest
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                    Button(
                        colors = ButtonDefaults.textButtonColors(),
                        onClick = onConfirm
                    ) {
                        Text(stringResource(R.string.ok))
                    }
                }
            }
        },
        onDismissRequest = onDismissRequest
    )
}

@Composable
@Preview
fun ProductDetailContentPreview() {
    ProductDetailContent(
        uiState = ProductDetailState(
            isLoading = false,
            product =
            ProductUI(
                id = 1,
                name = "Producto de Ejemplo",
                category = "Categoría A",
                price = 199.99,
                totalStock = 50,
                stockStatus = StockStatusUI.IN_STOCK
            )
        ),
        onUserEvent = {}
    )
}
