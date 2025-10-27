package com.uniandes.medisupply.data.remote.model.client

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewClientRequest(
    @SerialName("nombre")
    val name: String,
    @SerialName("tipo")
    val type: String,
    @SerialName("pais")
    val country: String,
    @SerialName("direccion")
    val address: String,
    val nit: String,
    @SerialName("correo_empresa")
    val companyEmail: String,
    @SerialName("nombre_contacto")
    val contactName: String,
    @SerialName("cargo_contacto")
    val contactPosition: String,
    @SerialName("telefono_contacto")
    val contactPhone: String,
    @SerialName("correo_contacto")
    val contactEmail: String
)
