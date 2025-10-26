package com.uniandes.medisupply.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.medisupply.R
import com.uniandes.medisupply.common.NavigationProvider
import com.uniandes.medisupply.common.ResourcesProvider
import com.uniandes.medisupply.domain.repository.ClientRepository
import com.uniandes.medisupply.common.isValidEmail
import com.uniandes.medisupply.common.isValidPhone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NewClientUiState(
    val name: String = "",
    val type: String = "",
    val contactName: String = "",
    val contactPhone: String = "",
    val contactEmail: String = "",
    val address: String = "",
    val position: String = "",
    val nit: String = "",
    val country: String = "",
    val companyEmail: String = "",
    val isLoading: Boolean = false,
    val showError: Boolean = false,
    val error: String? = null,
    val errorName: String? = null,
    val errorContactName: String? = null,
    val errorContactPhone: String? = null,
    val errorContactEmail: String? = null,
    val errorAddress: String? = null,
    val errorPosition: String? = null,
    val errorNit: String? = null,
    val errorCountry: String? = null,
    val errorCompanyEmail: String? = null,
    val primaryButtonEnabled: Boolean = false
)

class NewClientViewModel(
    private val clientRepository: ClientRepository,
    private val navigationProvider: NavigationProvider,
    private val resourcesProvider: ResourcesProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewClientUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: UserEvent) {
        when (event) {
            is UserEvent.OnNameChange -> {
                _uiState.update {
                    it.copy(
                        name = event.name,
                        errorName = if (event.name.isBlank()) resourcesProvider.getString(R.string.required_field) else null
                    )
                }
                checkButtonEnable()
            }

            is UserEvent.OnTypeChange -> {
                _uiState.update {
                    it.copy(
                        type = event.type
                    )
                }
                checkButtonEnable()
            }
            is UserEvent.OnContactNameChange -> {
                _uiState.update {
                    it.copy(contactName = event.contactName,
                        errorContactName = if (event.contactName.isBlank()) resourcesProvider.getString(R.string.required_field) else null
                    )
                }
                checkButtonEnable()
            }
            is UserEvent.OnContactPhoneChange -> {
                _uiState.update {
                    it.copy(contactPhone = event.contactPhone,
                        errorContactPhone = if (event.contactPhone.isValidPhone().not()) resourcesProvider.getString(R.string.required_field) else null
                    )
                }
            }
            is UserEvent.OnContactEmailChange -> {
                _uiState.update {
                    it.copy(contactEmail = event.contactEmail,
                        errorContactEmail = if (event.contactEmail.isValidEmail().not()) resourcesProvider.getString(R.string.required_field) else null
                    )
                }
                checkButtonEnable()
            }
            is UserEvent.OnAddressChange -> {
                _uiState.update {
                    it.copy(address = event.address,
                        errorAddress = if (event.address.isBlank()) resourcesProvider.getString(R.string.required_field) else null
                    )
                }
                checkButtonEnable()
            }
            is UserEvent.OnPositionChange -> {
                _uiState.update {
                    it.copy(position = event.position,
                        errorPosition = if (event.position.isBlank()) resourcesProvider.getString(R.string.required_field) else null
                    )
                }
                checkButtonEnable()
            }
            is UserEvent.OnSaveClientClick -> {
                onSaveClient()
            }
            is UserEvent.OnNitChange -> {
                val isValidNit = event.nit.isNotBlank() && event.nit.all { it.isDigit() } &&
                        event.nit.length in 9..10
                _uiState.update {
                    it.copy(nit = event.nit,
                        errorNit = if (isValidNit.not()) resourcesProvider.getString(R.string.required_field) else null
                    )
                }
                checkButtonEnable()
            }
            is UserEvent.OnCountryChange -> {
                _uiState.update {
                    it.copy(country = event.country)
                }
                checkButtonEnable()
            }
            is UserEvent.OnCompanyEmailChange -> {
                _uiState.update {
                    it.copy(companyEmail = event.companyEmail,
                        errorCompanyEmail = if (event.companyEmail.isValidEmail().not()) resourcesProvider.getString(R.string.required_field) else null
                    )
                }
                checkButtonEnable()
            }
            is UserEvent.OnDismissErrorDialog -> {
                _uiState.update {
                    it.copy(showError = false, error = null)
                }
            }
            is UserEvent.OnBackClick -> {
                navigationProvider.finishCurrentDestination()
            }
            else -> {}
        }
    }

    private fun checkButtonEnable(): Boolean {
        val state = uiState.value
        val validForm = state.name.isNotBlank() &&
                state.contactName.isNotBlank() &&
                state.contactPhone.isValidPhone() &&
                state.contactEmail.isValidEmail() &&
                state.address.isNotBlank() &&
                state.position.isNotBlank() &&
                state.nit.isNotBlank() &&
                state.country.isNotBlank() &&
                state.companyEmail.isValidEmail()
        _uiState.update {
            it.copy(primaryButtonEnabled = validForm)
        }
        return validForm
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
        data object OnBackClick : UserEvent()
        data object OnSaveClientClick : UserEvent()
    }
}
