package com.uniandes.medisupply.presentation.viewmodel.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.medisupply.common.InternalNavigator
import com.uniandes.medisupply.domain.model.Client
import com.uniandes.medisupply.domain.repository.ClientRepository
import com.uniandes.medisupply.presentation.navigation.Destination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class ClientsOrderState(
    val isLoading: Boolean = false,
    val clients: List<Client> = emptyList(),
    val error: String? = null,
    val showError: Boolean = false
)
class ClientListOrderViewModel(
    private val clientRepository: ClientRepository,
    private val internalNavigator: InternalNavigator
): ViewModel() {

    private val _clientsUiState = MutableStateFlow(
        ClientsOrderState(
            isLoading = true
        )
    )

    val clientsUiState = _clientsUiState.asStateFlow()

    init {
        viewModelScope.launch {
            val result = clientRepository.getClients()
            if (result.isSuccess) {
                val clients = result.getOrNull() ?: emptyList()
                _clientsUiState.update {
                    it.copy(
                        isLoading = false,
                        clients = clients
                    )
                }
            } else {
                _clientsUiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.localizedMessage ?: "Unknown error",
                        showError = true
                    )
                }
            }
        }
    }

    fun onEvent(event: UserEvent) {
        when(event) {
            is UserEvent.OnClientClicked -> {
                internalNavigator.navigateTo(
                    Destination.CreateOrder,
                    mapOf(Destination.CreateOrder.CLIENT to event.client)
                )
            }
            is UserEvent.OnBackClicked -> {
                internalNavigator.finishCurrentDestination()
            }
            is UserEvent.OnErrorDialogDismissed -> {
                _clientsUiState.update {
                    it.copy(
                        showError = false,
                        error = null
                    )
                }
            }
        }
    }

    sealed class UserEvent{
        data class OnClientClicked(val client: Client): UserEvent()
        data object OnBackClicked: UserEvent()
        data object OnErrorDialogDismissed: UserEvent()
    }
}