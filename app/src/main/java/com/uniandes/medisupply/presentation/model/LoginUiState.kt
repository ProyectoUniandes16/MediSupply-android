package com.uniandes.medisupply.presentation.model

data class LoginUiState(
    val isLoading: Boolean,
    val email: String,
    val password: String
)
