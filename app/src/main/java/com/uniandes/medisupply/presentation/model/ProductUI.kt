package com.uniandes.medisupply.presentation.model

import androidx.annotation.StringRes
import com.uniandes.medisupply.R
import com.uniandes.medisupply.domain.model.Product
import com.uniandes.medisupply.domain.model.StockStatus

data class ProductUI(
    val id: Int,
    val name: String,
    val price: Double,
    val totalStock: Int,
    val category: String,
    val stockStatus: StockStatusUI,
    val sku: String = "",
    val expirationDate: String = "",
    val storageInfo: String = "",
    val batchNumber: String = "",
    val status: String = "",
    val stock: List<StockUi> = emptyList()
) {
    companion object {
        fun fromDomain(
            product: Product
        ): ProductUI {
            return product.toUi()
        }
    }
}

data class StockUi(
    val location: String,
    val quantity: Int
)

enum class StockStatusUI(@StringRes val resId: Int) {
    IN_STOCK(R.string.in_stock),
    LOW_STOCK(R.string.low_stock),
    OUT_OF_STOCK(R.string.out_stock)
}

fun Product.toUi(): ProductUI {
    return ProductUI(
        id = this.id,
        name = this.name,
        price = this.price,
        totalStock = this.stock,
        category = this.category,
        stockStatus = this.stockStatus.toUi(),
        sku = this.sku ?: "",
        expirationDate = this.expirationDate ?: "",
        storageInfo = this.storageInfo ?: "",
        batchNumber = this.batchNumber ?: "",
        status = this.status ?: "",
        stock = this.stockDetails.map {
            StockUi(
                location = it.location,
                quantity = it.quantity
            )
        }
    )
}

fun StockStatus.toUi(): StockStatusUI {
    return when (this) {
        StockStatus.IN_STOCK -> StockStatusUI.IN_STOCK
        StockStatus.LOW_STOCK -> StockStatusUI.LOW_STOCK
        StockStatus.OUT_OF_STOCK -> StockStatusUI.OUT_OF_STOCK
    }
}
