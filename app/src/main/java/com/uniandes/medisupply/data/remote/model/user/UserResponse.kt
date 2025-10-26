package com.uniandes.medisupply.data.remote.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserWrapperResponse(
    val user: UserResponse,
    @SerialName("access_token")
    val accessToken: String
)

@Serializable
data class UserResponse(
    val id: Int,
    @SerialName("nombre")
    val name: String,
    @SerialName("apellido")
    val lastName: String,
    val rol: String,
    val email: String
)