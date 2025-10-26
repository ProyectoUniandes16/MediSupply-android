package com.uniandes.medisupply.data.remote.model.client

import com.uniandes.medisupply.data.remote.model.user.UserResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientWrapperResponse(
    @SerialName("cliente")
    val client: ClientResponse,
    @SerialName("access_token")
    val accessToken: String
)

@Serializable
data class ClientResponse(
    val id: Int,
    @SerialName("nombre")
    val name: String,
    @SerialName("tipo")
    val type: String,
    @SerialName("pais")
    val country: String,
    @SerialName("direccion")
    val address: String,
    val nit: String,
)

@Serializable
data class ClientCompleteResponse(
    @SerialName("nombre")
    val name: String,
    @SerialName("cargo")
    val position: String,
    @SerialName("telefono")
    val phone: String,
    @SerialName("correo")
    val email: String
)
