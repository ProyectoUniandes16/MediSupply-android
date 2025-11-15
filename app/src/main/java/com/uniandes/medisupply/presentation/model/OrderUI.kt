package com.uniandes.medisupply.presentation.model

import android.os.Parcelable
import androidx.annotation.StringRes
import com.uniandes.medisupply.R
import com.uniandes.medisupply.domain.model.Order
import com.uniandes.medisupply.domain.model.OrderStatus
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Parcelize
data class OrderUI(
    val id: Int,
    val clientId: Int,
    @StringRes
    val status: OrderStatusUI,
    val orderDate: String,
    val deliveryDate: String,
    val total: Double,
    val totalProducts: Int,
    val products: List<Pair<ProductUI, Int>> = emptyList()
) : Parcelable

fun Order.toUI() = OrderUI(
    id = id ?: 0,
    clientId = clientId,
    status = when (status) {
        OrderStatus.PENDING -> OrderStatusUI.PENDING
        OrderStatus.IN_PROGRESS -> OrderStatusUI.IN_PROGRESS
        OrderStatus.IN_TRANSIT -> OrderStatusUI.IN_TRANSIT
        OrderStatus.DELIVERED -> OrderStatusUI.DELIVERED
        else -> OrderStatusUI.CANCELED
    },
    orderDate = orderDate?.substringBefore('T') ?: "",
    deliveryDate = orderDate?.substringBefore('T')?.let { addTwoWeeks(it) } ?: "",
    total = total,
    totalProducts = totalProducts,
    products = products.map { (product, quantity) -> Pair(product.toUi(), quantity) }
)

enum class OrderStatusUI(val statusResId: Int) {
    PENDING(R.string.pending),
    IN_PROGRESS(R.string.in_progress),
    IN_TRANSIT(R.string.in_transit),
    DELIVERED(R.string.delivered),
    CANCELED(R.string.canceled)
}

private fun addTwoWeeks(dateString: String): String {
    if (dateString.isBlank()) return ""
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val date = LocalDate.parse(dateString, formatter)
    val newDate = date.plusWeeks(2)
    return newDate.format(formatter)
}
