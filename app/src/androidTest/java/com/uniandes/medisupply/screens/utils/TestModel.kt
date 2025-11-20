package com.uniandes.medisupply.screens.utils

import com.uniandes.medisupply.presentation.model.OrderStatusUI
import com.uniandes.medisupply.presentation.model.OrderUI
import com.uniandes.medisupply.presentation.model.ProductUI

val PRODUCT_UI = ProductUI(
    id = 1,
    name = "Product $1",
    price = 10.0,
    category = "Category"
)
val ORDER_UI = OrderUI(
    id = 1234,
    status = OrderStatusUI.DELIVERED,
    total = 150.0,
    clientId = 1,
    orderDate = "2025-09-16",
    deliveryDate = "2025-09-30",
    totalProducts = 30,
    products = List(5) {
        Pair(
            PRODUCT_UI.copy(
                id = it,
                name = "Product $it",
            ), it * 2
        )
    }
)
