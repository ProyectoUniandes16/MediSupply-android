package com.uniandes.medisupply.presentation.model

import android.os.Parcelable
import com.uniandes.medisupply.R
import com.uniandes.medisupply.domain.model.Order
import com.uniandes.medisupply.domain.model.OrderStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderUI(
    val id: Int,
    val clientId: Int,
    val status: OrderStatusUI,
    val orderDate: String,
    val deliveryDate: String,
    val total: Double,
    val totalProducts: Int,
    val products: List<Pair<ProductUI, Int>> = emptyList(),
    val client: ClientUI? = null
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
    products = products.map { (product, quantity) -> Pair(product.toUi(), quantity) },
    client = client?.toUI()
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
    return try {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        sdf.isLenient = false
        val parsed = sdf.parse(dateString) ?: return ""
        val cal = java.util.Calendar.getInstance().apply { time = parsed }
        cal.add(java.util.Calendar.DAY_OF_YEAR, 14)
        sdf.format(cal.time)
    } catch (e: Exception) {
        ""
    }
}
