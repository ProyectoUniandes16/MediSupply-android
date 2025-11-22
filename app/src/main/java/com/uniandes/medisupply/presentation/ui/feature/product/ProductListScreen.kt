package com.uniandes.medisupply.presentation.ui.feature.product

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uniandes.medisupply.R
import com.uniandes.medisupply.common.formatCurrency
import com.uniandes.medisupply.presentation.component.AlertDialog
import com.uniandes.medisupply.presentation.component.BackNavigation
import com.uniandes.medisupply.presentation.component.Card
import com.uniandes.medisupply.presentation.component.TopAppBar
import com.uniandes.medisupply.presentation.model.ProductUI
import com.uniandes.medisupply.presentation.model.StockStatusUI
import com.uniandes.medisupply.presentation.ui.theme.MediSupplyTheme
import com.uniandes.medisupply.presentation.viewmodel.product.ProductListUiState
import com.uniandes.medisupply.presentation.viewmodel.product.ProductListViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProductListScreen(
    modifier: Modifier = Modifier,
    viewModel: ProductListViewModel = koinViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    ProductListContent(
        modifier = modifier,
        uiState = uiState.value,
        onUserEvent = { viewModel.onEvent(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductListContent(
    modifier: Modifier = Modifier,
    uiState: ProductListUiState,
    onUserEvent: (ProductListViewModel.UserEvent) -> Unit = { }
) {
    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) {
            onUserEvent(ProductListViewModel.UserEvent.OnLoadProducts)
        }
    }

    Scaffold(
        topBar = {
            if (uiState.isStandAlone) {
                TopAppBar(
                    title = stringResource(R.string.products),
                    navigationIcon = BackNavigation {
                        onUserEvent(ProductListViewModel.UserEvent.OnBackClicked)
                    }
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.showError) {
                AlertDialog(
                    message = uiState.error
                        ?: stringResource(R.string.default_error_message),
                    onDismissRequest = {
                        onUserEvent(ProductListViewModel.UserEvent.OnDismissErrorDialog)
                    },
                    onConfirm = {
                        onUserEvent(ProductListViewModel.UserEvent.OnLoadProducts)
                    }
                )
            }

            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                var searchQuery by remember { mutableStateOf(uiState.filterQuery) }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = {
                            searchQuery = it
                            onUserEvent(ProductListViewModel.UserEvent.OnFilterQueryChange(it))
                        },
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    if (uiState.displayedProducts.isEmpty()) {
                        Box(Modifier.fillMaxSize()) {
                            Text(
                                text = stringResource(R.string.no_products_found),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(uiState.displayedProducts) { product ->
                                ProductCard(
                                    product = product,
                                    onClickItem = {
                                        onUserEvent(
                                            ProductListViewModel.UserEvent.OnProductClicked(it)
                                        )
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
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Buscar producto", color = Color.Gray) },
        leadingIcon = {
            Icon(Icons.Default.FavoriteBorder, contentDescription = "Favoritos", tint = Color.Gray)
        },
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        ),
        singleLine = true
    )
}

@Composable
fun ProductCard(
    modifier: Modifier = Modifier,
    product: ProductUI,
    onClickItem: (ProductUI) -> Unit
) {
    Card(
        modifier = modifier
        .fillMaxWidth().clickable { onClickItem(product) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = product.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                AssistChip(
                    onClick = { },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        brush = SolidColor(MaterialTheme.colorScheme.primary)
                    ),
                    modifier = Modifier.height(32.dp),
                    label = {
                        Text(
                            text = product.category.uppercase(),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.unit_price),
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Text(
                        text = product.price.formatCurrency(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Stock: ${product.totalStock}",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    AssistChip(
                        onClick = { },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            brush = SolidColor(MaterialTheme.colorScheme.onBackground)
                        ),
                        modifier = Modifier.height(36.dp),
                        label = {
                            Text(
                                text = stringResource(product.stockStatus.resId),
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductListEmptyScreenPreview() {
    MediSupplyTheme {
        ProductListContent(
            uiState = ProductListUiState(isLoading = false)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProductListScreenPreview() {
    MediSupplyTheme {
        ProductListContent(
            uiState = ProductListUiState(
                isLoading = false,
                products = List(5) {
                    ProductUI(
                        id = it,
                        name = "Producto $it",
                        price = 19900.0 + it * 1000,
                        totalStock = 10 - it * 2,
                        category = if (it % 2 == 0) "Medicamento" else "Equipo",
                        stockStatus = if (it % 3 == 0) StockStatusUI.IN_STOCK else if (it % 3 == 1) StockStatusUI.LOW_STOCK else StockStatusUI.OUT_OF_STOCK
                    )
                }
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProductListErrorScreenPreview() {
    MediSupplyTheme {
        ProductListContent(
            uiState = ProductListUiState(
                isLoading = false,
                showError = true,
                error = "Ha ocurrido un error al cargar los productos."
            )
        )
    }
}
