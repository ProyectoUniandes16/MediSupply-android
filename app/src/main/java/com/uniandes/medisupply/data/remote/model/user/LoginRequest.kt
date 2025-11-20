package com.uniandes.medisupply.data.remote.model.user

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)
