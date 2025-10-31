package com.uniandes.medisupply.data.remote.model.product

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductWrapperResponse(
    @SerialName("productos")
    val products: List<ProductResponse>
)

@Serializable
data class ProductResponse(
    @SerialName("cantidad_disponible")
    val availableStock: Int,
    @SerialName("categoria")
    val category: String,
    @SerialName("codigo_sku")
    val sku: String,
    @SerialName("condiciones_almacenamiento")
    val storageConditions: String,
    @SerialName("estado")
    val status: String,
    @SerialName("fecha_vencimiento")
    val expirationDate: String,
    @SerialName("id")
    val id: Int,
    @SerialName("nombre")
    val name: String,
    @SerialName("precio_unitario")
    val unitPrice: Double
)
