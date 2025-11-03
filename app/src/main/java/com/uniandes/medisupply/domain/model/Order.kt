package com.uniandes.medisupply.domain.model

import com.uniandes.medisupply.data.remote.model.order.PlaceOrderRequest
import com.uniandes.medisupply.data.remote.model.order.ProductOrderRequest

data class Order(
    val clientId: Int,
    val products: List<Pair<Product, Int>>,
    val total: Double,
    val status: String? = null
)

fun Order.toDataModel() = PlaceOrderRequest(
    clientId = clientId,
    products = products.toDataModel(),
    total = total
)

fun List<Pair<Product, Int>>.toDataModel() = map { (product, quantity) ->
    ProductOrderRequest(
        id = product.id,
        quantity = quantity,
        unitPrice = product.price
    )
}
