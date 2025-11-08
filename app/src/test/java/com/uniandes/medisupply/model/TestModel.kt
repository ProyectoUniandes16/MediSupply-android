package com.uniandes.medisupply.model

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