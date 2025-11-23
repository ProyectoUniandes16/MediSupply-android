package com.uniandes.medisupply.data.remote.model.catalog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ZoneResponse(
    @SerialName("nombre")
    val name: String
)
