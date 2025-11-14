package com.uniandes.medisupply.data.remote.model.client

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ContactResponse(
    @SerialName("cargo")
    val position: String,
    @SerialName("correo")
    val email: String,
    @SerialName("nombre")
    val name: String,
    @SerialName("telefono")
    val phone: String
)

@Serializable
data class ClientResponse(
    @SerialName("contacto")
    val contact: ContactResponse,
    @SerialName("correo")
    val email: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("direccion")
    val address: String,
    @SerialName("id")
    val id: Int,
    @SerialName("nit")
    val taxId: String,
    @SerialName("nombre")
    val name: String,
    @SerialName("zona")
    val country: String,
    @SerialName("tipo")
    val type: String,
    @SerialName("updated_at")
    val updatedAt: String
)
