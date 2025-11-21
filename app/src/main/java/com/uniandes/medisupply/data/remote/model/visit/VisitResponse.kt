package com.uniandes.medisupply.data.remote.model.visit

import com.uniandes.medisupply.data.remote.model.client.ClientResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VisitResponse(
    @SerialName("estado") val status: String,
    @SerialName("fecha_visita") val visitDate: String,
    @SerialName("id_visita") val id: Int,
    @SerialName("cliente") val client: ClientResponse,
)
