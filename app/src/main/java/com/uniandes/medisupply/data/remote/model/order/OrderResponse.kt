package com.uniandes.medisupply.data.remote.model.order

import com.uniandes.medisupply.data.remote.model.client.ClientResponse
import com.uniandes.medisupply.data.remote.model.product.ProductResponse
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
    val totalProducts: Int = 0,
    @SerialName("vendedor_id")
    val sellerId: String,
    @SerialName("productos")
    val products: List<ProductOrderResponse> = emptyList(),
    @SerialName("cliente")
    val client: ClientResponse? = null
)

@Serializable
data class ProductOrderResponse(
    @SerialName("cantidad")
    val quantity: Int,
    @SerialName("producto")
    val product: ProductResponse
)
