package com.uniandes.medisupply.presentation.viewmodel.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.medisupply.common.InternalNavigator
import com.uniandes.medisupply.domain.repository.ProductRepository
import com.uniandes.medisupply.presentation.model.ProductUI
import com.uniandes.medisupply.presentation.navigation.ProductDestination.ProductDetail.PRODUCT
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductDetailState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val product: ProductUI
)

class ProductDetailViewModel(
    private val productRepository: ProductRepository,
    private val internalNavigator: InternalNavigator
) : ViewModel() {

    private val product: ProductUI = internalNavigator.getParam(PRODUCT) as? ProductUI ?: run {
        throw IllegalArgumentException("ProductDetailViewModel requires a ProductUI parameter")
    }
    private val _uiState = MutableStateFlow(
        ProductDetailState(
            isLoading = true,
            product = product
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        loadProductDetail()
    }

    private fun loadProductDetail() {
        _uiState.update {
            it.copy(isLoading = true, error = null)
        }
        viewModelScope.launch {
            val result = productRepository.getProductById(product.id)
            result.onSuccess {
                val productUI = ProductUI.fromDomain(it)
                _uiState.update { state ->
                    state.copy(isLoading = false, product = productUI)
                }
            }
            result.onFailure {
                _uiState.update { state ->
                    state.copy(isLoading = false, error = it.message)
                }
            }
        }
    }

    fun onEvent(event: OnEvent) {
        when (event) {
            OnEvent.OnBackClicked -> {
                internalNavigator.stepBack()
            }
            OnEvent.OnAddVideoClicked -> {
                // Handle add video click event
            }
        }
    }

    sealed class OnEvent {
        data object OnBackClicked : OnEvent()
        data object OnAddVideoClicked : OnEvent()
    }
}
