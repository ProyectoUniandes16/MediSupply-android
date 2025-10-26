package com.uniandes.medisupply.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.medisupply.R
import com.uniandes.medisupply.presentation.containers.HomeClientActivity.Companion.USER_KEY
import com.uniandes.medisupply.common.AppDestination
import com.uniandes.medisupply.common.NavigationProvider
import com.uniandes.medisupply.common.ResourcesProvider
import com.uniandes.medisupply.common.UserDataProvider
import com.uniandes.medisupply.domain.repository.UserRepository
import com.uniandes.medisupply.presentation.model.LoginUiState
import com.uniandes.medisupply.common.isValidEmail
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val navigationProvider: NavigationProvider,
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
            navigationProvider.requestDestination(
                AppDestination.HomeClient()
            )
            navigationProvider.finishCurrentDestination()
        }
    }

    fun onEvent(event: UserEvent) {
        when (event) {
            is UserEvent.OnPrimaryButtonClick -> {
                login()
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
                        error = null
                    )
                }
            }
            else -> {}
        }
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
                userDataProvider.setAccessToken(it.second)
                userDataProvider.setUserLoggedIn(true)
                navigationProvider.requestDestination(
                    AppDestination.HomeClient(
                        extraMap = mapOf(USER_KEY to it.first)
                    )
                )
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
    }
}
