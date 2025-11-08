package com.uniandes.medisupply.presentation.ui.feature.product

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uniandes.medisupply.R
import com.uniandes.medisupply.common.formatCurrency
import com.uniandes.medisupply.presentation.component.BackNavigation
import com.uniandes.medisupply.presentation.component.Card
import com.uniandes.medisupply.presentation.component.TopAppBar
import com.uniandes.medisupply.presentation.model.ProductUI
import com.uniandes.medisupply.presentation.model.StockStatusUI
import com.uniandes.medisupply.presentation.ui.theme.spaces
import com.uniandes.medisupply.presentation.viewmodel.product.ProductDetailState
import com.uniandes.medisupply.presentation.viewmodel.product.ProductDetailViewModel
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
    onUserEvent: (ProductDetailViewModel.OnEvent) -> Unit,
    uiState: ProductDetailState
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = "",
                navigationIcon = BackNavigation {
                    onUserEvent(ProductDetailViewModel.OnEvent.OnBackClicked)
                }
            )
        },
        content = { paddingValues ->
            ProductDetailView(
                modifier = Modifier.padding(paddingValues),
                uiState = uiState
            )
        },
        bottomBar = {
            Button(
                modifier = Modifier.padding(MaterialTheme.spaces.medium).fillMaxWidth(),
                onClick = {
                    onUserEvent(ProductDetailViewModel.OnEvent.OnAddVideoClicked)
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
                            text = stringResource(R.string.stock),
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

        Spacer(modifier = Modifier.height(MaterialTheme.spaces.medium))

        // Recommendation Videos Card
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
        }

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

@Composable
private fun VideoItem(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Reproducir video",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
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
