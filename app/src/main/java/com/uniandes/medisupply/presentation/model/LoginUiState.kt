package com.uniandes.medisupply.presentation.model

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
    val showHiddenDialog: Boolean = false
)
