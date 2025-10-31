package com.uniandes.medisupply.presentation.viewmodel.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.medisupply.common.InternalNavigator
import com.uniandes.medisupply.domain.model.Client
import com.uniandes.medisupply.domain.model.Order
import com.uniandes.medisupply.domain.model.Product
import com.uniandes.medisupply.domain.repository.OrderRepository
import com.uniandes.medisupply.domain.repository.ProductRepository
import com.uniandes.medisupply.presentation.navigation.Destination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateOrderUiState(
    val showProductBottomSheet: Boolean = false,
    val isConfirmation: Boolean = false,
    val productList: List<Product> = emptyList(),
    val productOrder: List<Pair<Product, Int>> = emptyList(),
    val totalAmount: Double = 0.0,
    val isLoadingConfirmation: Boolean = false,
    val showError: Boolean = false,
    val errorMessage: String? = null
)

class CreateOrderViewModel(
    private val internalNavigator: InternalNavigator,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateOrderUiState())
    val uiState = _uiState.asStateFlow()
    val client: Client = internalNavigator.getParam(Destination.CreateOrder.CLIENT) as Client

    fun onEvent(event: UserEvent) {
        when (event) {
            is UserEvent.OnBackClicked -> {
                internalNavigator.stepBack()
            }
            is UserEvent.OnAddProductClicked -> {
                _uiState.update { it.copy(showProductBottomSheet = true) }
                viewModelScope.launch {
                    val result = productRepository.getProducts()
                    if (result.isSuccess) {
                        val currentOrderProducts = uiState.value.productOrder.map { it.first.id }
                        val availableProducts = result.getOrNull()?.filterNot { currentOrderProducts.contains(it.id) } ?: emptyList()
                        _uiState.update {
                            it.copy(
                                productList = availableProducts.sortedBy { p -> p.name }
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                productList = emptyList()
                            )
                        }
                    }
                }
            }
            is UserEvent.OnDismissProductBottomSheet -> {
                _uiState.value = uiState.value.copy(
                    showProductBottomSheet = false
                )
            }
            is UserEvent.OnConfirmClicked -> {
                confirmOrder()
            }
            is UserEvent.OnCompleteClicked -> {
                _uiState.update {
                    it.copy(
                        isConfirmation = true
                    )
                }
            }
            is UserEvent.OnProductSelected -> {
                onProductSelected(event.product)
            }
            is UserEvent.OnIncreaseQuantityClicked -> {
                increaseProductQuantity(event.product)
            }
            is UserEvent.OnDecreaseQuantityClicked -> {
                decreaseProductQuantity(event.product)
            }
            is UserEvent.OnEditOrderClicked -> {
                _uiState.update {
                    it.copy(
                        isConfirmation = false
                    )
                }
            }
        }
    }

    private fun confirmOrder() {
        _uiState.update {
            it.copy(isLoadingConfirmation = true)
        }
        viewModelScope.launch {
            val order = Order(
                clientId = client.id,
                products = uiState.value.productOrder,
                total = uiState.value.totalAmount
            )
            val result = orderRepository.placeOrder(order)
            if (result.isSuccess) {
                internalNavigator.finishCurrentDestination(
                    success = true
                )
            } else {
                _uiState.update {
                    it.copy(
                        isLoadingConfirmation = false,
                        showError = true,
                        errorMessage = result.exceptionOrNull()?.localizedMessage
                    )
                }
            }
        }
    }

    private fun onProductSelected(product: Product) {
        _uiState.update {
            it.copy(
                productOrder = it.productOrder + (product to 1),
                productList = it.productList.filterNot { p -> p.id == product.id }.sortedBy { p -> p.name },
                showProductBottomSheet = false,
                totalAmount = calculateTotalAmount(it.productOrder + (product to 1))
            )
        }
    }

    private fun increaseProductQuantity(product: Product) {
        val currentOrder = uiState.value.productOrder.toMutableList()
        val index = currentOrder.indexOfFirst { it.first.id == product.id }
        if (index != -1) {
            val currentProduct = currentOrder[index]
            if (currentProduct.first.stock >= currentProduct.second + 1) {
                currentOrder[index] = currentProduct.copy(second = currentProduct.second + 1)
            }
            _uiState.update {
                it.copy(
                    productOrder = currentOrder,
                    totalAmount = calculateTotalAmount(currentOrder)
                )
            }
        }
    }

    private fun decreaseProductQuantity(product: Product) {
        val currentOrder = uiState.value.productOrder.toMutableList()
        val index = currentOrder.indexOfFirst { it.first.id == product.id }
        if (index != -1) {
            val currentProduct = currentOrder[index]
            if (currentProduct.second - 1 <= 0) {
                currentOrder.removeAt(index)
            } else {
                currentOrder[index] = currentProduct.copy(second = currentProduct.second - 1)
            }
            _uiState.update {
                it.copy(
                    productOrder = currentOrder,
                    totalAmount = calculateTotalAmount(currentOrder),
                    productList = if (currentProduct.second - 1 <= 0) {
                        it.productList + currentProduct.first
                    } else {
                        it.productList
                    }.sortedBy { p -> p.name }
                )
            }
        }
    }

    private fun calculateTotalAmount(productOrder: List<Pair<Product, Int>>): Double {
        return productOrder.sumOf { it.first.price.times(it.second) }
    }

    sealed class UserEvent {
        data object OnBackClicked : UserEvent()
        data object OnAddProductClicked : UserEvent()
        data object OnDismissProductBottomSheet : UserEvent()
        data object OnConfirmClicked : UserEvent()
        data object OnCompleteClicked : UserEvent()
        data class OnProductSelected(val product: Product) : UserEvent()
        data class OnIncreaseQuantityClicked(val product: Product) : UserEvent()
        data class OnDecreaseQuantityClicked(val product: Product) : UserEvent()
        data object OnEditOrderClicked : UserEvent()
    }
}
