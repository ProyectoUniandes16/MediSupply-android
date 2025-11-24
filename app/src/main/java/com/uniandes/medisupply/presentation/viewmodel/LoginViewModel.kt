package com.uniandes.medisupply.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.medisupply.R
import com.uniandes.medisupply.presentation.containers.HomeClientActivity.Companion.USER_KEY
import com.uniandes.medisupply.common.AppDestination
import com.uniandes.medisupply.common.InternalNavigator
import com.uniandes.medisupply.common.ResourcesProvider
import com.uniandes.medisupply.common.UserDataProvider
import com.uniandes.medisupply.domain.repository.UserRepository
import com.uniandes.medisupply.common.isValidEmail
import com.uniandes.medisupply.presentation.navigation.Destination
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean,
    val email: String,
    val password: String,
    val showError: Boolean = false,
    val error: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val loginButtonEnable: Boolean = false,
    val baseUrl: String = "",
    val showHiddenDialog: Boolean = false,
    val isLogin: Boolean = true,
) {
    val titleStringId = if (isLogin) R.string.login else R.string.new_user
    val primaryButtonStringId = if (isLogin) R.string.login else R.string.create_account
    val secondaryButtonStringId = if (isLogin) R.string.not_registered_create_account else R.string.have_account_login
}

class LoginViewModel(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val internalNavigator: InternalNavigator,
    private val userRepository: UserRepository,
    private val userDataProvider: UserDataProvider,
    private val resourcesProvider: ResourcesProvider
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        LoginUiState(isLoading = false, email = "", password = "")
    )
    val uiState = _uiState.asStateFlow()

    init {
        if (userDataProvider.isUserLoggedIn()) {
            internalNavigator.requestDestination(
                AppDestination.HomeClient()
            )
            internalNavigator.finishCurrentDestination()
        }
    }

    fun onEvent(event: UserEvent) {
        when (event) {
            is UserEvent.OnPrimaryButtonClick -> {
                if (_uiState.value.isLogin) {
                    login()
                } else {
                    createClient()
                }
            }
            is UserEvent.OnEmailChange -> {
                onEmailChange(event.email)
            }
            is UserEvent.OnPasswordChange -> {
                onPasswordChange(event.password)
            }
            is UserEvent.OnDismissErrorDialog -> {
                _uiState.update {
                    it.copy(
                        showError = false,
                        error = null,
                        showHiddenDialog = false
                    )
                }
            }
            is UserEvent.OnHiddenAccess -> {
                _uiState.update {
                    it.copy(
                        showHiddenDialog = true
                    )
                }
            }
            is UserEvent.OnBaseUrlChange -> {
                _uiState.update {
                    it.copy(
                        baseUrl = event.baseUrl
                    )
                }
            }
            is UserEvent.OnSaveBaseUrl -> {
                _uiState.update {
                    it.copy(
                        showHiddenDialog = false
                    )
                }
                userDataProvider.setBaseUrl(_uiState.value.baseUrl)
            }

            is UserEvent.OnSecondaryButtonClick -> {
               /* internalNavigator.navigateTo(
                    Destination.NewClient,
                    mapOf(Destination.NewClient.IS_NEW_USER to true)
                )*/
                _uiState.update {
                    it.copy(
                        isLogin = !it.isLogin,
                    )
                }
            }

            else -> {
            }
        }
    }

    private fun createClient() {
        val params = mapOf(
            Destination.NewClient.PRE_FILLED_EMAIL to _uiState.value.email,
            Destination.NewClient.PRE_FILLED_PASSWORD to _uiState.value.password,
            Destination.NewClient.IS_NEW_USER to true
        )
        internalNavigator.navigateTo(
            Destination.NewClient,
            params
        )
    }

    private fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                passwordError = if (password.isNotEmpty()) null else resourcesProvider.getString(R.string.password_empty)
            )
        }
        checkLoginButtonEnable()
    }

    private fun onEmailChange(email: String) {
        val isValidEmail = email.isValidEmail()
        _uiState.update {
            it.copy(
                email = email,
                emailError = if (isValidEmail) null else resourcesProvider.getString(R.string.not_valid_email)
            )
        }
        checkLoginButtonEnable()
    }

    private fun checkLoginButtonEnable() {
        _uiState.update {
            it.copy(
                loginButtonEnable = it.email.isValidEmail() && it.password.isNotEmpty()
            )
        }
    }

    private fun login() {
        _uiState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch(ioDispatcher) {
            val result = userRepository.login(
                _uiState.value.email,
                _uiState.value.password
            )
            result.onSuccess {
                userDataProvider.setUserData(it.second, it.first)
                internalNavigator.requestDestination(
                    AppDestination.HomeClient(
                        extraMap = mapOf(USER_KEY to it.first)
                    )
                )
                internalNavigator.finishCurrentDestination()
                _uiState.update { state ->
                    state.copy(isLoading = false)
                }
            }
            result.onFailure { error ->
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

    sealed class UserEvent {
        data object OnPrimaryButtonClick : UserEvent()
        data class OnEmailChange(val email: String) : UserEvent()
        data class OnPasswordChange(val password: String) : UserEvent()
        data object OnDismissErrorDialog : UserEvent()
        data object OnHiddenAccess : UserEvent()
        data class OnBaseUrlChange(val baseUrl: String) : UserEvent()
        data object OnSaveBaseUrl : UserEvent()
        data object OnSecondaryButtonClick : UserEvent()
    }
}
