package com.uniandes.medisupply.domain.model

import com.uniandes.medisupply.data.remote.model.order.OrderResponse
import com.uniandes.medisupply.data.remote.model.order.PlaceOrderRequest
import com.uniandes.medisupply.data.remote.model.order.ProductOrderRequest

data class Order(
    val id: Int? = null,
    val clientId: Int,
    val products: List<Pair<Product, Int>>,
    val total: Double,
    val status: OrderStatus? = null,
    val totalProducts: Int = products.sumOf { it.second },
    val orderDate: String? = null
)

enum class OrderStatus(private val status: String) {
    PENDING("pendiente"),
    IN_PROGRESS("en_proceso"),
    IN_TRANSIT("despachado"),
    DELIVERED("entregado"),
    CANCELED("cancelado");

   companion object {
       fun fromString(status: String): OrderStatus? {
           return entries.find { it.status == status }
       }
   }
}
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

fun OrderResponse.toDomain() = Order(
    clientId = clientId,
    products = products.map { Pair(it.product.toDomain(), it.quantity) },
    total = total,
    status = OrderStatus.fromString(status),
    totalProducts = totalProducts,
    orderDate = orderDate,
    id = id,
)
