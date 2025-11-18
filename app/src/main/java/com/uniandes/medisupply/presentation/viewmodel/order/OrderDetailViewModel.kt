package com.uniandes.medisupply.presentation.viewmodel.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.medisupply.common.InternalNavigator
import com.uniandes.medisupply.domain.repository.OrderRepository
import com.uniandes.medisupply.presentation.model.OrderUI
import com.uniandes.medisupply.presentation.model.toUI
import com.uniandes.medisupply.presentation.navigation.Destination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OrderDetailUiState(
    val isLoading: Boolean = true,
    val showError: Boolean = false,
    val error: String? = null,
    val order: OrderUI
)

class OrderDetailViewModel(
    private val orderRepository: OrderRepository,
    private val internalNavigator: InternalNavigator
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        OrderDetailUiState(
            isLoading = false,
            order = internalNavigator.getParam(Destination.OrderDetail.ORDER) as OrderUI
        )
    )

    val uiState = _uiState.asStateFlow()

    init {
        loadOrderDetails()
    }

    private fun loadOrderDetails() {
        val orderId = _uiState.value.order.id
        _uiState.value = _uiState.value.copy(isLoading = true, showError = false, error = null)
        viewModelScope.launch {
            orderRepository.getOrderById(orderId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        order = it.toUI()
                    )
                }.onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        showError = true,
                        error = it.message
                    )
                }
        }
    }

    fun onEvent(event: UserEvent) {
        when (event) {
            is UserEvent.OnBackClicked -> {
                internalNavigator.stepBack()
            }
        }
    }

    sealed class UserEvent {
       data object OnBackClicked : UserEvent()
    }
}
