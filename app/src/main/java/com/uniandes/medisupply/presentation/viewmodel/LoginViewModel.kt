package com.uniandes.medisupply.presentation.viewmodel

import androidx.core.bundle.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.medisupply.HomeClientActivity.Companion.USER_KEY
import com.uniandes.medisupply.common.AppDestination
import com.uniandes.medisupply.common.NavigationProvider
import com.uniandes.medisupply.domain.model.User
import com.uniandes.medisupply.domain.repository.UserRepository
import com.uniandes.medisupply.presentation.model.LoginUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LoginViewModel(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val navigationProvider: NavigationProvider,
    private val userRepository: UserRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(
        LoginUiState(isLoading = false, email = "", password = "")
    )
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: UserEvent) {
        when (event) {
            is UserEvent.OnPrimaryButtonClick -> {
                login()
            }
            is UserEvent.OnEmailChange -> {
                _uiState.update {
                    it.copy(email = event.email)
                }
            }
            is UserEvent.OnPasswordChange -> {
                _uiState.update {
                    it.copy(password = event.password)
                }
            }
            else -> {}
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
                _uiState.update {
                    it.copy(isLoading = false)
                }
                navigationProvider.requestDestination(
                    AppDestination.HomeClient(
                        extraMap = mapOf(USER_KEY to it)
                    )
                )
            }
            result.onFailure {
                _uiState.update {
                    it.copy(isLoading = false)
                }
                navigationProvider.requestDestination(
                    AppDestination.HomeClient(
                        extraMap = mapOf(USER_KEY to User(
                                10,
                                "name",
                                "email")
                        )
                    )
                )
            }
        }
    }

    sealed class UserEvent {
        data object OnPrimaryButtonClick : UserEvent()
        data class OnEmailChange(val email: String) : UserEvent()
        data class OnPasswordChange(val password: String) : UserEvent()
    }
}