package com.uniandes.medisupply.domain.model

import com.uniandes.medisupply.data.remote.model.product.ProductResponse
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val stock: Int,
    val category: String,
    val stockStatus: StockStatus,
    val sku: String? = null,
    val expirationDate: String? = null,
    val storageInfo: String? = null,
    val batchNumber: String? = null,
    val status: String? = null,
    val stockDetails: List<StockDetail> = emptyList()
)

@Serializable
data class StockDetail(
    val location: String,
    val quantity: Int
)

enum class StockStatus {
    IN_STOCK,
    LOW_STOCK,
    OUT_OF_STOCK
}


fun ProductResponse.toDomain(): Product {
    val stockDetails = this.stockDetails.map {
        StockDetail(
            location = it.location,
            quantity = it.quantity
        )
    }
    val sumStockDetails = sumStockDetails(stockDetails)
    val stockStatusUI = run { calculateStockStatus(
        if (stockDetails.isEmpty()) {
            this.availableStock
        } else {
            sumStockDetails
        }
    ) }
    return Product(
        id = this.id,
        name = this.name,
        price = this.unitPrice,
        stock = if (stockDetails.isEmpty()) {
            this.availableStock
        } else {
            sumStockDetails
        },
        category = this.category,
        stockStatus = stockStatusUI,
        sku = this.sku,
        expirationDate = this.expirationDate,
        storageInfo = this.storageConditions,
        stockDetails = stockDetails
    )
}

private fun sumStockDetails(stockDetails: List<StockDetail>): Int {
    return stockDetails.sumOf { it.quantity }
}

private fun calculateStockStatus(stock: Int): StockStatus {
    return when {
        stock > 10 -> StockStatus.IN_STOCK
        stock in 1..10 -> StockStatus.LOW_STOCK
        else -> StockStatus.OUT_OF_STOCK
    }
}