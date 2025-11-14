package com.uniandes.medisupply.data.remote.model.order

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class OrderResponse(
    @SerialName("cliente_id")
    val clientId: Int,
    @SerialName("estado")
    val status: String,
    @SerialName("fecha_pedido")
    val orderDate: String,
    @SerialName("id")
    val id: Int,
    @SerialName("total")
    val total: Double,
    @SerialName("total_productos")
    val totalProducts: Int,
    @SerialName("vendedor_id")
    val sellerId: String
)
