package com.uniandes.medisupply.model

import com.uniandes.medisupply.data.remote.model.order.OrderResponse
import com.uniandes.medisupply.domain.model.Order
import com.uniandes.medisupply.domain.model.Product
import com.uniandes.medisupply.domain.model.StockStatus
import com.uniandes.medisupply.presentation.model.ProductUI

val PRODUCT = Product(
    id = 0,
    name = "Product",
    price = 10.0,
    stock = 100,
    stockStatus = StockStatus.IN_STOCK,
    category = "category"
)
val PRODUCT_LIST = List(5) {
    PRODUCT.copy(
        id = it,
        name = "Product $it"
    )
}

val PRODUCT_UI = ProductUI.fromDomain(PRODUCT)

val TEST_ORDER = Order(
    id = 0,
    clientId = 1,
    status = null,
    orderDate = "2024-01-01T00:00:00Z",
    total = 100.0,
    totalProducts = 10,
    products = emptyList()
)

val TEST_ORDER_RESPONSE = OrderResponse(
    id = 0,
    clientId = 1,
    status = "pendiente",
    orderDate = "2024-01-01T00:00:00Z",
    total = 100.0,
    totalProducts = 10,
    sellerId = "101010"
)
