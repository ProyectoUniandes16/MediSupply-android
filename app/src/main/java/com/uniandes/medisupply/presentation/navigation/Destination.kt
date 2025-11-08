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
    data object NewClient : Destination("NewClient")
    @Serializable
    data object ClientListOrder : Destination("ClientListOrder")
    @Serializable
    data object OrderList : Destination("OrderList")
    @Serializable
    data object CreateOrder : Destination("ClientProductOrder") {
        const val CLIENT = "CLIENT"
    }
}

@Serializable
sealed class ProductDestination(private val productRoute: String) : Destination(productRoute) {
    @Serializable
    data object ProductList : ProductDestination("ProductList")
    @Serializable
    data object ProductDetail : ProductDestination("ProductDetail") {
        const val PRODUCT = "PRODUCT"
    }
}
