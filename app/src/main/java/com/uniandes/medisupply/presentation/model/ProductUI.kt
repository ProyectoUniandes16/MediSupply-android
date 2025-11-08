package com.uniandes.medisupply.presentation.model

import com.uniandes.medisupply.domain.model.Product

data class ProductUI(
    val id: Int,
    val name: String,
    val price: Double,
    val stock: Int,
    val category: String,
    val stockStatus: String
) {
    companion object {
        fun fromDomain(
            product: Product
        ): ProductUI {
            return product.let {
                ProductUI(
                    id = it.id,
                    name = it.name,
                    price = it.price,
                    stock = it.stock,
                    category = it.category,
                    stockStatus = ""
                )
            }
        }
    }
}
