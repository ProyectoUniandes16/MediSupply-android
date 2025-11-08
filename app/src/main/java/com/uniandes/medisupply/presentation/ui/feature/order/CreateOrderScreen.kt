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
import androidx.compose.material.Scaffold
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uniandes.medisupply.R
import com.uniandes.medisupply.domain.model.Client
import com.uniandes.medisupply.domain.model.ClientContactInfo
import com.uniandes.medisupply.domain.model.Product
import com.uniandes.medisupply.presentation.component.AvatarText
import com.uniandes.medisupply.presentation.component.BackNavigation
import com.uniandes.medisupply.presentation.component.TopAppBar
import com.uniandes.medisupply.presentation.ui.theme.spaces
import com.uniandes.medisupply.presentation.viewmodel.order.CreateOrderUiState
import com.uniandes.medisupply.presentation.viewmodel.order.CreateOrderViewModel
import org.koin.compose.viewmodel.koinViewModel

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
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isProductLoading) {
                    CircularProgressIndicator()
                } else {
                    if (uiState.productList.isEmpty()) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(MaterialTheme.spaces.medium),
                            text = stringResource(R.string.no_more_products),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(uiState.productList) { product ->
                                ProductItem(
                                    product = product,
                                    onItemClick = { selectedProduct ->
                                        onEvent(CreateOrderViewModel.UserEvent.OnProductSelected(selectedProduct))
                                    }
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
    productOrder: List<Pair<Product, Int>>,
    isConfirmation: Boolean,
    onAddProductClicked: () -> Unit,
    onEditOrderClicked: () -> Unit,
    onIncreaseClicked: (product: Product) -> Unit,
    onDecreaseClicked: (product: Product) -> Unit
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
                            },
                            isConfirmation = isConfirmation
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
    productItem: Pair<Product, Int>,
    isConfirmation: Boolean,
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
                onClick = onDecreaseClicked,
                enabled = isConfirmation.not()
            ) {
                Icon(painterResource(R.drawable.remove), contentDescription = stringResource(R.string.remove))
            }
            Text("${productItem.second}", modifier = Modifier.align(Alignment.CenterVertically))
            IconButton(
                onClick = onIncreaseClicked,
                enabled = isConfirmation.not()
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.add))
            }
        }
    }
}

@Composable
private fun ProductItem(
    modifier: Modifier = Modifier,
    product: Product,
    onItemClick: (Product) -> Unit
) {
    val mModifier = if (product.stock > 0) {
        modifier.clickable {
            onItemClick(product)
        }
    } else modifier
    Row(
        modifier = mModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(MaterialTheme.spaces.small)
        ) {
            Text(product.name, style = MaterialTheme.typography.titleMedium)
            Text(stringResource(R.string.price_value, product.price.toString()))
        }
        Text(
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
            text = stringResource(R.string.available_stock, product.stock.toString()))
    }
}

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
