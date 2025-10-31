package com.uniandes.medisupply.data.remote.model.order

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaceOrderRequest(
    @SerialName("cliente_id")
    val clientId: Int,
    @SerialName("productos")
    val products: List<ProductOrderRequest>,
    @SerialName("total")
    val total: Double
)

@Serializable
data class ProductOrderRequest(
    @SerialName("id")
    val id: Int,
    @SerialName("cantidad")
    val quantity: Int,
    @SerialName("precio")
    val unitPrice: Double
)