package com.uniandes.medisupply.presentation.navigation
import kotlinx.serialization.Serializable

@Serializable
sealed class Destination(val route: String) {
    @Serializable
    data object Login : Destination("login")
    @Serializable
    data object HomeVendor : Destination("HomeVendor")
}