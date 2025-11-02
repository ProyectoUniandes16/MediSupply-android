package com.uniandes.medisupply.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val stock: Int
)
