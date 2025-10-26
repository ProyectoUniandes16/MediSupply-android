package com.uniandes.medisupply.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.medisupply.common.NavigationProvider
import com.uniandes.medisupply.domain.repository.ClientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NewClientUiState(
    val name: String = "Clinica A",
    val type: String = "Clinica",
    val contactName: String = "jose perez",
    val contactPhone: String = "5533325122",
    val contactEmail: String = "joseperez@clinicaa.com",
    val address: String = "av siempre viva 123",
    val position: String = "Gerente",
    val nit: String = "00000002",
    val country: String = "mexico",
    val companyEmail: String = "admin@clinicaa.com",
    val isLoading: Boolean = false,
    val showError: Boolean = false,
    val error: String? = null
)

class NewClientViewModel(
    private val clientRepository: ClientRepository,
    private val navigationProvider: NavigationProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewClientUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: UserEvent) {
        when (event) {
            is UserEvent.OnNameChange -> {
                _uiState.update {
                    it.copy(name = event.name)
                }
            }

            is UserEvent.OnTypeChange -> {
                _uiState.update {
                    it.copy(type = event.type)
                }
            }
            is UserEvent.OnContactNameChange -> {
                _uiState.update {
                    it.copy(contactName = event.contactName)
                }
            }
            is UserEvent.OnContactPhoneChange -> {
                _uiState.update {
                    it.copy(contactPhone = event.contactPhone)
                }
            }
            is UserEvent.OnContactEmailChange -> {
                _uiState.update {
                    it.copy(contactEmail = event.contactEmail)
                }
            }
            is UserEvent.OnAddressChange -> {
                _uiState.update {
                    it.copy(address = event.address)
                }
            }
            is UserEvent.OnPositionChange -> {
                _uiState.update {
                    it.copy(position = event.position)
                }
            }
            is UserEvent.OnSaveClientClick -> {
                onSaveClient()
            }
            is UserEvent.OnNitChange -> {
                _uiState.update {
                    it.copy(nit = event.nit)
                }
            }
            is UserEvent.OnCountryChange -> {
                _uiState.update {
                    it.copy(country = event.country)
                }
            }
            is UserEvent.OnCompanyEmailChange -> {
                _uiState.update {
                    it.copy(companyEmail = event.companyEmail)
                }
            }
            is UserEvent.OnDismissErrorDialog -> {
                _uiState.update {
                    it.copy(showError = false, error = null)
                }
            }
            else -> {}
        }
    }

    private fun onSaveClient() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = clientRepository.addClient(
                name = uiState.value.name,
                type = uiState.value.type,
                nit = uiState.value.nit,
                address = uiState.value.address,
                country = uiState.value.country,
                contactName = uiState.value.contactName,
                contactPosition = uiState.value.position,
                contactPhone = uiState.value.contactPhone,
                contactEmail = uiState.value.contactEmail,
                companyEmail = uiState.value.companyEmail
            )

            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, showError = false) }
                navigationProvider.finishCurrentDestination(
                    success = true
                )
            } else {
                _uiState.update { it.copy(isLoading = false, showError = true, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    sealed class UserEvent {
        data class OnNameChange(val name: String) : UserEvent()
        data class OnTypeChange(val type: String) : UserEvent()
        data class OnContactNameChange(val contactName: String) : UserEvent()
        data class OnContactPhoneChange(val contactPhone: String) : UserEvent()
        data class OnContactEmailChange(val contactEmail: String) : UserEvent()
        data class OnAddressChange(val address: String) : UserEvent()
        data class OnPositionChange(val position: String) : UserEvent()
        data class OnNitChange(val nit: String) : UserEvent()
        data class OnCountryChange(val country: String) : UserEvent()
        data class OnCompanyEmailChange(val companyEmail: String) : UserEvent()
        data object OnDismissErrorDialog : UserEvent()
        data object OnSaveClientClick : UserEvent()
    }
}
