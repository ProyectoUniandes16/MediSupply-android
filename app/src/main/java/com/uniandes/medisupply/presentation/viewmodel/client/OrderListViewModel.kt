package com.uniandes.medisupply.presentation.viewmodel.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.medisupply.common.AppDestination
import com.uniandes.medisupply.common.InternalNavigator
import com.uniandes.medisupply.domain.repository.OrderRepository
import com.uniandes.medisupply.presentation.containers.ComposableFlow
import com.uniandes.medisupply.presentation.model.OrderStatusUI
import com.uniandes.medisupply.presentation.model.OrderUI
import com.uniandes.medisupply.presentation.model.toUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrderListUiState(
    val isLoading: Boolean = true,
    private val orders: List<OrderUI> = emptyList(),
    val error: String? = null,
    val hasError: Boolean = false,
    val selectedStatus: OrderStatusUI = OrderStatusUI.PENDING
) {
    val displayedOrders: List<OrderUI> = orders.filter { it.status == selectedStatus }
}

class OrderListViewModel(
    private val orderRepository: OrderRepository,
    private val internalNavigator: InternalNavigator
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        OrderListUiState(
            isLoading = true
        )
    )
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: UserEvent) {
        when (event) {
            is UserEvent.LoadOrders -> {
                loadOrders()
            }
            is UserEvent.OnFilterChanged -> {
                _uiState.update {
                    it.copy(selectedStatus = event.status)
                }
            }
            is UserEvent.OnOrderClicked -> {
                internalNavigator.requestDestination(
                    appDestination = AppDestination.ComposableDestination(
                        flow = ComposableFlow.OrderFlow(event.order)
                    )
                )
            }
        }
    }

    private fun loadOrders() {
        _uiState.update { it.copy(isLoading = true, hasError = false, error = null) }
        viewModelScope.launch {
            orderRepository.getOrders()
                .onSuccess { orders ->
                    _uiState.value = OrderListUiState(
                        isLoading = false,
                        orders = orders.map { it.toUI() }
                    )
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.localizedMessage ?: "Unknown error",
                            hasError = true
                        )
                    }
                }
        }
    }

    sealed class UserEvent {
        data object LoadOrders : UserEvent()
        data class OnFilterChanged(val status: OrderStatusUI) : UserEvent()
        data class OnOrderClicked(val order: OrderUI) : UserEvent()
    }
}
