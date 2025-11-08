package com.uniandes.medisupply.presentation.viewmodel.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.medisupply.R
import com.uniandes.medisupply.common.InternalNavigator
import com.uniandes.medisupply.common.ResourcesProvider
import com.uniandes.medisupply.domain.model.StockStatus
import com.uniandes.medisupply.domain.repository.ProductRepository
import com.uniandes.medisupply.presentation.model.ProductUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProductListUiState(
    val isLoading: Boolean = true,
    val showError: Boolean = false,
    val error: String? = null,
    private val products: List<ProductUI> = emptyList(),
    val filterQuery: String = "",
) {
    val displayedProducts: List<ProductUI> = if (filterQuery.isEmpty()) products else products.filter {
        it.name.contains(filterQuery, ignoreCase = true)
    }
}
class ProductListViewModel(
    private val productRepository: ProductRepository,
    resourcesProvider: ResourcesProvider,
    private val internalNavigator: InternalNavigator
) : ViewModel() {

    private val _uiState: MutableStateFlow<ProductListUiState> =
        MutableStateFlow(ProductListUiState())
    val uiState = _uiState.asStateFlow()
    private val lowStockString = resourcesProvider.getString(R.string.low_stock)
    private val inStockString = resourcesProvider.getString(R.string.in_stock)
    private val outOfStockString = resourcesProvider.getString(R.string.out_stock)

    fun onEvent(event: UserEvent) {
        when (event) {
            UserEvent.OnLoadProducts -> {
                loadProducts()
            }
            UserEvent.OnBackClicked -> {
                internalNavigator.stepBack()
            }
            is UserEvent.OnFilterQueryChange -> {
                _uiState.value = _uiState.value.copy(
                    filterQuery = event.query,
                )
            }
            UserEvent.OnDismissErrorDialog -> {
                _uiState.value = _uiState.value.copy(
                    showError = false,
                    error = null
                )
            }
            else -> {
            }
        }
    }

    private fun loadProducts() {
        _uiState.value = _uiState.value.copy(isLoading = true)
       viewModelScope.launch {
           productRepository.getProducts().onSuccess { products ->
               val uiProducts = products.map {
                   ProductUI.fromDomain(it)
                       .copy(
                           stockStatus = when (it.stockStatus) {
                               StockStatus.LOW_STOCK -> lowStockString
                               StockStatus.IN_STOCK -> inStockString
                               StockStatus.OUT_OF_STOCK -> outOfStockString
                           }
                       )
               }
               _uiState.value = _uiState.value.copy(
                   isLoading = false,
                   products = uiProducts
               )
           }.onFailure { error ->
               _uiState.value = _uiState.value.copy(
                   isLoading = false,
                   showError = true,
                   error = error.message
               )
           }
       }
    }

    sealed class UserEvent {
        data object OnLoadProducts : UserEvent()
        data object OnBackClicked : UserEvent()
        data class OnFilterQueryChange(val query: String) : UserEvent()
        data object OnDismissErrorDialog : UserEvent()
    }
}
