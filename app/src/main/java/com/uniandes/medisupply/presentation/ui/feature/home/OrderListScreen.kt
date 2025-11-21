package com.uniandes.medisupply.presentation.ui.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.uniandes.medisupply.R
import com.uniandes.medisupply.common.ExcludeFromJacocoGeneratedReport
import com.uniandes.medisupply.common.formatCurrency
import com.uniandes.medisupply.presentation.component.AlertDialog
import com.uniandes.medisupply.presentation.model.OrderStatusUI
import com.uniandes.medisupply.presentation.model.OrderUI
import com.uniandes.medisupply.presentation.ui.theme.spaces
import com.uniandes.medisupply.presentation.viewmodel.client.OrderListUiState
import com.uniandes.medisupply.presentation.viewmodel.client.OrderListViewModel
import org.koin.compose.viewmodel.koinViewModel

@ExcludeFromJacocoGeneratedReport
@Composable
fun OrderListScreen(
    modifier: Modifier = Modifier,
    viewModel: OrderListViewModel = koinViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    OrderListContent(
        modifier = modifier,
        uiState = uiState.value,
        onEvent = { event ->
            viewModel.onEvent(event)
        }
    )
}

@Composable
internal fun OrderListContent(
    modifier: Modifier = Modifier,
    uiState: OrderListUiState,
    onEvent: (OrderListViewModel.UserEvent) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            onEvent(OrderListViewModel.UserEvent.LoadOrders)
        }
    }
    if (uiState.hasError) {
        AlertDialog(
            title = stringResource(R.string.default_error_title),
            message = uiState.error ?: stringResource(R.string.default_error),
            onDismissRequest = {
                onEvent(OrderListViewModel.UserEvent.LoadOrders)
            },
            confirmButtonText = null,
            onConfirm = {
                onEvent(OrderListViewModel.UserEvent.LoadOrders)
            },
            dismissButtonText = stringResource(R.string.retry),
        )
    }
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                Modifier.testTag("LOADING")
            )
        } else {
            var selectedTab by remember { mutableIntStateOf(0) }
            val tabs = listOf(
                Pair(stringResource(R.string.pending), OrderStatusUI.PENDING),
                Pair(stringResource(R.string.in_progress), OrderStatusUI.IN_PROGRESS),
                Pair(stringResource(R.string.in_transit), OrderStatusUI.IN_TRANSIT),
                Pair(stringResource(R.string.delivered), OrderStatusUI.DELIVERED),
            )

            Column(
                modifier = modifier
                    .fillMaxSize()
            ) {
                // Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F1FA)),
                    containerColor = Color(0xFFF5F1FA),
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            height = 3.dp,
                            color = Color(0xFF6B5B95)
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, pair ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = {
                                selectedTab = index
                                onEvent(OrderListViewModel.UserEvent.OnFilterChanged(pair.second))
                            },
                            text = {
                                Text(
                                    text = pair.first,
                                    color = if (selectedTab == index) Color(0xFF6B5B95) else Color(0xFF999999),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 0.5.dp)

                if (uiState.displayedOrders.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = MaterialTheme.spaces.medium),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(
                            top = MaterialTheme.spaces.medium,
                            bottom = MaterialTheme.spaces.xLarge +
                                    MaterialTheme.spaces.xLarge +
                                    MaterialTheme.spaces.xLarge
                        )
                    ) {
                        items(uiState.displayedOrders) {
                            OrderCard(
                                it,
                                onItemClick = {
                                    onEvent(OrderListViewModel.UserEvent.OnOrderClicked(it))
                                }
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_orders_found),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }

            FloatingActionButton(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomEnd),
                onClick = {
                    onEvent(OrderListViewModel.UserEvent.OnNewOrderClicked)
                }
            ) {
                Text(
                    modifier = Modifier.padding(MaterialTheme.spaces.medium),
                    text = stringResource(R.string.new_order)
                )
            }
        }
    }
}

@Composable
fun OrderCard(
    order: OrderUI,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onItemClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.id.let { "#$it" } ?: "N/A",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                AssistChip(
                    onClick = { },
                    modifier = Modifier.height(32.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFF6B5B95)),
                    label = {
                        Text(
                            text = stringResource(order.status.statusResId).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "${stringResource(R.string.date)}: ${order.orderDate}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = "${stringResource(R.string.delivery)}: ${order.deliveryDate}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.n_products, order.totalProducts),
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = order.total.formatCurrency("USD"),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrdersScreenPreview() {
    MaterialTheme {
        val uiState = OrderListUiState(
            isLoading = false
        )
        OrderListContent(
            uiState = uiState,
            onEvent = {
            }
        )
    }
}

@ExcludeFromJacocoGeneratedReport
@Preview(showBackground = true)
@Composable
fun OrdersScreenErrorPreview() {
    MaterialTheme {
        val uiState = OrderListUiState(
            isLoading = false,
            error = "An error occurred",
            hasError = true
        )
        OrderListContent(
            uiState = uiState,
            onEvent = {
            }
        )
    }
}
