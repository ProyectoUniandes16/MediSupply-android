package com.uniandes.medisupply.presentation.ui.feature.order

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uniandes.medisupply.R
import com.uniandes.medisupply.common.formatCurrency
import com.uniandes.medisupply.presentation.component.AlertDialog
import com.uniandes.medisupply.presentation.model.OrderStatusUI
import com.uniandes.medisupply.presentation.model.OrderUI
import com.uniandes.medisupply.presentation.model.ProductUI
import com.uniandes.medisupply.presentation.viewmodel.order.OrderDetailUiState
import com.uniandes.medisupply.presentation.viewmodel.order.OrderDetailViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OrderDetailScreen(
    viewModel: OrderDetailViewModel = koinViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    OrderDetailContent(
        uiState = uiState.value,
        onBackClick = {
            viewModel.onEvent(OrderDetailViewModel.UserEvent.OnBackClicked)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailContent(
    uiState: OrderDetailUiState,
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.Share, "Share")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE8D4F8)
                )
            )
        }
    ) { paddingValues ->

        if (uiState.showError) {
            AlertDialog(
                title = stringResource(R.string.default_error),
                message = uiState.error ?: stringResource(R.string.default_error_message),
                confirmButtonText = null,
                dismissButtonText = stringResource(R.string.ok),
                onDismissRequest = {
                    onBackClick()
                },
                onConfirm = {
                    onBackClick()
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "#${uiState.order.id}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        AssistChip(
                            onClick = { },
                            modifier = Modifier.height(32.dp),
                            label = {
                                Text(
                                    stringResource(uiState.order.status.statusResId),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Black
                                )
                            }
                        )
                    }
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            InfoRow(
                                icon = Icons.Default.DateRange,
                                label = stringResource(R.string.date),
                                value = "2025-09-16"
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            InfoRow(
                                icon = Icons.Default.Place,
                                label = stringResource(R.string.delivery),
                                value = "2025-09-30"
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            InfoRow(
                                icon = Icons.Default.Place,
                                label = stringResource(R.string.address),
                                value = "Ave. Siempre Viva 742, Springfield."
                            )
                        }
                    }
                }

                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.products),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = uiState.order.total.formatCurrency("USD"),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6750A4)
                            )
                        }
                    }
                }

                if (uiState.isLoading) {
                    item {
                        CircularProgressIndicator(
                            Modifier.testTag("LOADING")
                        )
                    }
                } else {
                    items(uiState.order.products) { (product, quantity) ->
                        ProductItem(product, quantity)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(20.dp)
                .padding(top = 2.dp),
            tint = Color(0xFF6750A4)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ProductItem(product: ProductUI, quantity: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color.White)
            .padding(vertical = 12.dp, horizontal = 16.dp)

    ) {
        Text(
            text = product.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "$quantity",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            color = Color(0xFFEEEEEE)
        )
    }
}

@Preview
@Composable
fun OrderDetailScreenPreview() {
    MaterialTheme {
        OrderDetailContent(
            uiState = OrderDetailUiState(
                order = OrderUI(
                    id = 1234,
                    status = OrderStatusUI.DELIVERED,
                    total = 150.0,
                    clientId = 1,
                    orderDate = "2025-09-16",
                    deliveryDate = "2025-09-30",
                    totalProducts = 30,
                    products = List(5) {
                        Pair(
                            ProductUI(
                                id = it,
                                name = "Product $it",
                                price = 10.0,
                                category = "Category",
                            ), it * 2
                        )
                    }
                )
            )
        )
    }
}
