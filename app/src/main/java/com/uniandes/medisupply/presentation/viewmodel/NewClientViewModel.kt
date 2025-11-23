package com.uniandes.medisupply.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.medisupply.R
import com.uniandes.medisupply.common.AppDestination
import com.uniandes.medisupply.common.InternalNavigator
import com.uniandes.medisupply.common.ResourcesProvider
import com.uniandes.medisupply.common.UserDataProvider
import com.uniandes.medisupply.domain.repository.ClientRepository
import com.uniandes.medisupply.common.isValidEmail
import com.uniandes.medisupply.common.isValidPhone
import com.uniandes.medisupply.domain.model.ClientType
import com.uniandes.medisupply.domain.model.Zone
import com.uniandes.medisupply.domain.repository.CatalogRepository
import com.uniandes.medisupply.domain.repository.UserRepository
import com.uniandes.medisupply.presentation.containers.HomeClientActivity.Companion.USER_KEY
import com.uniandes.medisupply.presentation.navigation.Destination
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
    val primaryButtonEnabled: Boolean = false,
    val clientTypes: Map<ClientType, String> = emptyMap(),
    val countryList: List<String> = Zone.entries.toList().map { it.displayName },
    val isNewUser: Boolean = false,
    val showCompanyEmailField: Boolean = true
)

class NewClientViewModel(
    private val clientRepository: ClientRepository,
    private val internalNavigator: InternalNavigator,
    private val resourcesProvider: ResourcesProvider,
    private val userRepository: UserRepository,
    private val userDataProvider: UserDataProvider,
    private val catalogRepository: CatalogRepository
) : ViewModel() {

    private val isNewUser: Boolean = internalNavigator.getParam(Destination.NewClient.IS_NEW_USER) as? Boolean ?: false
    private val _uiState = MutableStateFlow(NewClientUiState(
        clientTypes = ClientType.entries.associateWith { type ->
            when (type) {
                ClientType.IPS -> resourcesProvider.getString(R.string.ips)
                ClientType.OTHER -> resourcesProvider.getString(R.string.other)
                ClientType.CLINIC -> resourcesProvider.getString(R.string.clinic)
                ClientType.DISTRIBUTOR -> resourcesProvider.getString(R.string.distributor)
                ClientType.EPS_EAPB -> resourcesProvider.getString(R.string.eps_eapb)
                ClientType.HOSPITAL -> resourcesProvider.getString(R.string.hospital)
                ClientType.LABORATORY -> resourcesProvider.getString(R.string.laboratory)
            }
        },
        isNewUser = isNewUser,
        companyEmail = if (isNewUser) internalNavigator.getParam(Destination.NewClient.PRE_FILLED_EMAIL) as? String ?: "" else "",
        showCompanyEmailField = isNewUser.not()
    ))
    val uiState = _uiState.asStateFlow()
    private val password = internalNavigator.getParam(Destination.NewClient.PRE_FILLED_PASSWORD) as? String ?: "".apply {
        if (isNewUser)
        throw IllegalArgumentException("Password must be provided for new user")
    }

    init {
        fetchZones()
    }

    private fun fetchZones() {
        viewModelScope.launch {
            catalogRepository.getZones()
                .onSuccess { zones ->
                    _uiState.update {
                        it.copy(
                            countryList = zones
                        )
                    }
                }.onFailure {
                }
        }
    }

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
                        errorContactPhone = if (event.contactPhone.isValidPhone().not()) resourcesProvider.getString(R.string.invalid_phone) else null
                    )
                }
            }
            is UserEvent.OnContactEmailChange -> {
                _uiState.update {
                    it.copy(contactEmail = event.contactEmail,
                        errorContactEmail = if (event.contactEmail.isValidEmail().not()) resourcesProvider.getString(R.string.invalid_email) else null
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
                _uiState.update { it.copy(isLoading = true) }
                if (isNewUser.not()) {
                    onSaveClient()
                } else {
                    signUpClient()
                }
            }
            is UserEvent.OnNitChange -> {
                val isValidNit = isValidNit(event.nit)
                _uiState.update {
                    it.copy(nit = event.nit,
                        errorNit = if (isValidNit.not()) resourcesProvider.getString(R.string.invalid_nit) else null
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
                        errorCompanyEmail = if (event.companyEmail.isValidEmail().not()) resourcesProvider.getString(R.string.invalid_email) else null
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
                internalNavigator.stepBack()
            }
            else -> {}
        }
    }

    private fun isValidNit(nit: String): Boolean {
        return nit.isNotBlank() && nit.all { it.isDigit() } &&
                nit.length in 9..10
    }

    private fun checkButtonEnable(): Boolean {
        val state = uiState.value
        val validForm = state.name.isNotBlank() &&
                state.contactName.isNotBlank() &&
                state.contactPhone.isValidPhone() &&
                state.contactEmail.isValidEmail() &&
                state.address.isNotBlank() &&
                state.position.isNotBlank() &&
                isValidNit(state.nit) &&
                state.country.isNotBlank() &&
                state.companyEmail.isValidEmail()
        _uiState.update {
            it.copy(primaryButtonEnabled = validForm)
        }
        return validForm
    }

    private fun onSaveClient() {
        viewModelScope.launch {
            val result = clientRepository.addClient(
                name = uiState.value.name,
                type = resolveType(uiState.value.type).displayName,
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
                internalNavigator.finishCurrentDestination(
                    success = true
                )
            } else {
                _uiState.update { it.copy(isLoading = false, showError = true, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun signUpClient() {
        viewModelScope.launch {
            val result = userRepository.signUpClient(
                name = uiState.value.name,
                companyEmail = uiState.value.companyEmail,
                password = password,
                contactName = uiState.value.contactName,
                contactEmail = uiState.value.contactEmail,
                contactPhone = uiState.value.contactPhone,
                address = uiState.value.address,
                nit = uiState.value.nit,
                zone = uiState.value.country,
                type = resolveType(uiState.value.type).displayName,
                contactPosition = uiState.value.position
            ).onSuccess { userWithToken ->
                userDataProvider.setUserData(userWithToken.second, userWithToken.first)
                internalNavigator.requestDestination(
                    AppDestination.HomeClient(
                        extraMap = mapOf(USER_KEY to userWithToken.first)
                    )
                )
                _uiState.update { it.copy(isLoading = false, showError = false) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        showError = true,
                        error = error.message
                    )
                }
            }
        }
    }

    private fun resolveType(type: String): ClientType {
        return uiState.value.clientTypes.entries.firstOrNull { it.value == type }?.key ?: throw IllegalArgumentException("Invalid client type")
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
