package com.uniandes.medisupply.presentation.navigation
import kotlinx.serialization.Serializable

@Serializable
sealed class Destination(val route: String) {
    @Serializable
    data object Login : Destination("login")
    @Serializable
    data object HomeVendor : Destination("HomeVendor")
    @Serializable
    data object ClientList : Destination("ClientList")
    @Serializable
    data object NewClient : Destination("NewClient") {
        const val PRE_FILLED_EMAIL = "PRE_FILLED_EMAIL"
        const val PRE_FILLED_PASSWORD = "PRE_FILLED_PASSWORD"
        const val IS_NEW_USER = "IS_NEW_USER"
    }
    @Serializable
    data object ClientListOrder : Destination("ClientListOrder")
    @Serializable
    data object VendorOrderList : Destination("VendorOrderList")
    @Serializable
    data object CreateOrder : Destination("ClientProductOrder") {
        const val CLIENT = "CLIENT"
    }
    @Serializable
    data object OrderDetail : Destination("OrderDetail") {
        const val ORDER = "ORDER"
    }
}

@Serializable
sealed class HomeClientDestination(private val clientRoute: String) : Destination(clientRoute) {
    @Serializable
    data object ClientOrderList : HomeClientDestination("ClientOrderList")

    @Serializable
    data object ProductList : HomeClientDestination("ProductList")
}

@Serializable
sealed class ProductDestination(private val productRoute: String) : Destination(productRoute) {
    @Serializable
    data object ProductList : ProductDestination("ProductList") {
        const val IS_STANDALONE = "IS_STANDALONE"
    }
    @Serializable
    data object ProductDetail : ProductDestination("ProductDetail") {
        const val PRODUCT = "PRODUCT"
    }
}
