package com.uniandes.medisupply.data.remote.model.visit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateVisitStatusRequest(
    @SerialName("estado")
    val status: String
)
