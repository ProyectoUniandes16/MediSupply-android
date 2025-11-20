package com.uniandes.medisupply.presentation.model

import android.os.Parcelable
import androidx.annotation.StringRes
import com.uniandes.medisupply.R
import com.uniandes.medisupply.domain.model.Product
import com.uniandes.medisupply.domain.model.StockStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductUI(
    val id: Int,
    val name: String,
    val price: Double,
    val totalStock: Int = 0,
    val category: String,
    val stockStatus: StockStatusUI = StockStatusUI.OUT_OF_STOCK,
    val sku: String = "",
    val expirationDate: String = "",
    val storageInfo: String = "",
    val batchNumber: String = "",
    val status: String = "",
    val stock: List<StockUi> = emptyList(),
    val availableStock: Int = 0,
) : Parcelable {
    companion object {
        fun fromDomain(
            product: Product
        ): ProductUI {
            return product.toUi()
        }
    }
}

@Parcelize
data class StockUi(
    val location: String,
    val quantity: Int
) : Parcelable

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
        },
        availableStock = this.stock
    )
}

fun StockStatus.toUi(): StockStatusUI {
    return when (this) {
        StockStatus.IN_STOCK -> StockStatusUI.IN_STOCK
        StockStatus.LOW_STOCK -> StockStatusUI.LOW_STOCK
        StockStatus.OUT_OF_STOCK -> StockStatusUI.OUT_OF_STOCK
    }
}

fun ProductUI.toDomain(): Product {
    return Product(
        id = this.id,
        name = this.name,
        price = this.price,
        stock = this.totalStock,
        category = this.category,
        stockStatus = StockStatus.IN_STOCK,
        sku = this.sku.ifBlank { null },
        expirationDate = if (this.expirationDate.isBlank()) null else this.expirationDate,
        storageInfo = if (this.storageInfo.isBlank()) null else this.storageInfo,
        batchNumber = if (this.batchNumber.isBlank()) null else this.batchNumber,
        status = if (this.status.isBlank()) null else this.status,
    )
}
