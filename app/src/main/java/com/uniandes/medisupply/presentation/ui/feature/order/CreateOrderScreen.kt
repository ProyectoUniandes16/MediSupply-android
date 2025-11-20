package com.uniandes.medisupply.presentation.ui.feature.order

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uniandes.medisupply.R
import com.uniandes.medisupply.common.ExcludeFromJacocoGeneratedReport
import com.uniandes.medisupply.domain.model.Client
import com.uniandes.medisupply.domain.model.ClientContactInfo
import com.uniandes.medisupply.presentation.component.AvatarText
import com.uniandes.medisupply.presentation.component.BackNavigation
import com.uniandes.medisupply.presentation.component.TopAppBar
import com.uniandes.medisupply.presentation.model.ProductUI
import com.uniandes.medisupply.presentation.ui.theme.spaces
import com.uniandes.medisupply.presentation.viewmodel.order.CreateOrderUiState
import com.uniandes.medisupply.presentation.viewmodel.order.CreateOrderViewModel
import org.koin.compose.viewmodel.koinViewModel

@ExcludeFromJacocoGeneratedReport
@Composable
fun ClientOrderScreen(
    viewModel: CreateOrderViewModel = koinViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    ClientOrderContent(
        client = viewModel.client,
        uiState = uiState.value,
        onEvent = { event ->
            viewModel.onEvent(event)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientOrderContent(
    modifier: Modifier = Modifier,
    client: Client,
    uiState: CreateOrderUiState,
    onEvent: (CreateOrderViewModel.UserEvent) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                navigationIcon = BackNavigation {
                    onEvent(CreateOrderViewModel.UserEvent.OnBackClicked)
                }
            )
        },
        bottomBar = {
            if (uiState.isConfirmation) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isLoadingConfirmation) {
                        CircularProgressIndicator(
                            Modifier.size(50.dp)
                                .testTag("CONFIRMATION_ORDER_LOADING_INDICATOR")
                        )
                    } else {
                        Button(
                            modifier = Modifier
                                .padding(MaterialTheme.spaces.medium)
                                .fillMaxWidth(),
                            onClick = {
                                onEvent(CreateOrderViewModel.UserEvent.OnConfirmClicked)
                            }
                        ) {
                            Text(stringResource(R.string.confirm_order))
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(MaterialTheme.spaces.medium),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spaces.large)

            ) {
                ClientInfoOrderHeader(
                    client = client,
                    totalAmount = uiState.totalAmount,
                    orderSize = uiState.productOrder.map { it.second }.sum()
                )
                OrderProductList(
                    onAddProductClicked = {
                        onEvent(CreateOrderViewModel.UserEvent.OnAddProductClicked)
                    },
                    productOrder = uiState.productOrder,
                    onIncreaseClicked = { product ->
                        onEvent(CreateOrderViewModel.UserEvent.OnIncreaseQuantityClicked(product))
                    },
                    onDecreaseClicked = { product ->
                        onEvent(CreateOrderViewModel.UserEvent.OnDecreaseQuantityClicked(product))
                    },
                    onEditOrderClicked = {
                        onEvent(CreateOrderViewModel.UserEvent.OnEditOrderClicked)
                    },
                    isConfirmation = uiState.isConfirmation
                )
            }
            if (uiState.isConfirmation.not() && uiState.productOrder.isNotEmpty()) {
                FloatingActionButton(
                    onClick = {
                        onEvent(CreateOrderViewModel.UserEvent.OnCompleteClicked)
                    },
                    modifier = Modifier
                        .padding(MaterialTheme.spaces.medium)
                ) {
                    Text(
                        text = stringResource(R.string.complete_order),
                        modifier = Modifier.padding(MaterialTheme.spaces.small)
                    )
                }
            }
        }
    }
    if (uiState.showProductBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxSize(),
            onDismissRequest = { onEvent(CreateOrderViewModel.UserEvent.OnDismissProductBottomSheet) },
            sheetState = sheetState
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    stringResource(R.string.product_list),
                    style = MaterialTheme.typography.titleMedium
                )
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (uiState.isLoadingProducts) {
                        CircularProgressIndicator(
                            Modifier.align(Alignment.Center)
                        )
                    } else {
                        if (uiState.productList.isNotEmpty()) {
                            LazyColumn {
                                items(uiState.productList) { product ->
                                    ProductItem(
                                        onItemClick = {
                                            onEvent(CreateOrderViewModel.UserEvent.OnProductSelected(it))
                                        },
                                        product = product
                                    )
                                }
                            }
                        } else {
                            Text(
                                stringResource(R.string.no_products_found),
                                modifier = Modifier.align(Alignment.Center),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientInfoOrderHeader(
    client: Client,
    totalAmount: Double,
    orderSize: Int,
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(MaterialTheme.spaces.medium)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    space = MaterialTheme.spaces.medium
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarText(
                    client.name,
                )
                Text(
                    client.name,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Text(stringResource(R.string.products_added, orderSize))
            Text(stringResource(R.string.total_price, totalAmount.toString()))
        }
    }
}

@Composable
private fun OrderProductList(
    modifier: Modifier = Modifier,
    productOrder: List<Pair<ProductUI, Int>>,
    isConfirmation: Boolean,
    onAddProductClicked: () -> Unit,
    onEditOrderClicked: () -> Unit,
    onIncreaseClicked: (product: ProductUI) -> Unit,
    onDecreaseClicked: (product: ProductUI) -> Unit
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isConfirmation.not()) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.spaces.small),
                    onClick = onAddProductClicked
                ) {
                    Text(stringResource(R.string.add_product))
                }
            } else {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.spaces.small),
                    onClick = onEditOrderClicked
                ) {
                    Text(stringResource(R.string.edit_order))
                }
            }
            if (productOrder.isEmpty()) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.spaces.medium),
                    text = stringResource(R.string.empty_order),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            } else {
                LazyColumn {
                    items(productOrder) { productItem ->
                        ProductOrderItem(
                            productItem = productItem,
                            onIncreaseClicked = {
                                onIncreaseClicked(productItem.first)
                            },
                            onDecreaseClicked = {
                                onDecreaseClicked(productItem.first)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductOrderItem(
    modifier: Modifier = Modifier,
    productItem: Pair<ProductUI, Int>,
    onIncreaseClicked: () -> Unit,
    onDecreaseClicked: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(
                horizontal = MaterialTheme.spaces.medium
            ),
        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(productItem.first.name)
            Text(stringResource(R.string.price_value, productItem.first.price.toString()))
        }
        Row {
            IconButton(
                onClick = onDecreaseClicked
            ) {
                Icon(painterResource(R.drawable.remove), contentDescription = stringResource(R.string.remove))
            }
            Text("${productItem.second}", modifier = Modifier.align(Alignment.CenterVertically))
            IconButton(
                onClick = onIncreaseClicked
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.add))
            }
        }
    }
}

@Composable
private fun ProductItem(
    modifier: Modifier = Modifier,
    product: ProductUI,
    onItemClick: (ProductUI) -> Unit
) {
    Row(
        modifier = modifier.clickable {
            onItemClick(product)
        }.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            modifier = modifier
                .weight(1f)
                .padding(MaterialTheme.spaces.small)
        ) {
            Text(product.name, style = MaterialTheme.typography.titleMedium)
            Text(stringResource(R.string.price_value, product.price.toString()))
        }
        Text(
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
            text = stringResource(R.string.available_stock, product.availableStock.toString()))
    }
}

@ExcludeFromJacocoGeneratedReport
@Composable
@Preview
fun ClientOrderScreenPreview() {
    ClientOrderContent(
        client = Client(
            id = 1,
            name = "Juan Perez",
            address = "Calle 123",
            contactInfo = ClientContactInfo(
                phone = "1234567890",
                email = "",
                position = "Manager",
                name = "Manager"
            ),
            email = ""
        ),
        uiState = CreateOrderUiState(),
        onEvent = {
        }
    )
}
