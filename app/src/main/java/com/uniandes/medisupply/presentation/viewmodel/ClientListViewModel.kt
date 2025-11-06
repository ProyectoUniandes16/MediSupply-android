package com.uniandes.medisupply.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.medisupply.common.AppDestination
import com.uniandes.medisupply.common.NavigationProvider
import com.uniandes.medisupply.domain.model.Client
import com.uniandes.medisupply.domain.repository.ClientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClientListUiState(
    val isLoading: Boolean = true,
    val showError: Boolean = false,
    val error: String? = null,
    val clients: List<Client> = emptyList()
)

class ClientListViewModel(
    private val navigationProvider: NavigationProvider,
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<ClientListUiState> =
        MutableStateFlow(ClientListUiState())
    val uiState = _uiState.asStateFlow()

    private fun loadClients() {
        _uiState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            val result = clientRepository.getClients()
            result.onSuccess { clients ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        clients = clients
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        showError = true,
                        error = it.error
                    )
                }
            }
        }
    }

    fun onEvent(event: UserEvent) {
        when (event) {
            is UserEvent.OnNewClientClick -> {
                navigationProvider.requestDestination(
                    AppDestination.NewClient,
                    requestResultCode = AppDestination.NewClient.REQUEST_CODE
                )
            }
            is UserEvent.OnRefreshClients -> {
                loadClients()
            }
            is UserEvent.OnDismissErrorDialog -> {
                _uiState.update {
                    it.copy(
                        showError = false,
                        error = null
                    )
                }
            }
            is UserEvent.OnClientOrderClicked -> {
                navigationProvider.requestDestination(
                    AppDestination.NewOrder(
                        event.client
                    ),
                    requestResultCode = AppDestination.NewOrder.REQUEST_CODE
                )
            }
        }
    }

    sealed class UserEvent {
        data object OnNewClientClick : UserEvent()
        data object OnRefreshClients : UserEvent()
        data object OnDismissErrorDialog : UserEvent()
        data class OnClientOrderClicked(val client: Client) : UserEvent()
    }
}
