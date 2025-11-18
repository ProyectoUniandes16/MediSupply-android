package com.uniandes.medisupply.common

import com.uniandes.medisupply.domain.model.Client
import com.uniandes.medisupply.presentation.containers.ComposableFlow
import com.uniandes.medisupply.presentation.navigation.Destination

sealed class
AppDestination(val extras: Map<String, Any> = emptyMap()) {
    class HomeClient(extraMap: Map<String, Any> = emptyMap()) : AppDestination(extraMap)
    data object NewClient : AppDestination() {
        const val REQUEST_CODE = 1001
    }
    data class NewOrder(
        val client: Client? = null
    ) : AppDestination(
        extras = client?.let { mapOf(Destination.CreateOrder.CLIENT to it) } ?: emptyMap()
    ) {
        companion object {
            const val REQUEST_CODE = 1002
        }
    }

    data class ComposableDestination(
        val flow: ComposableFlow,
        val extraMap: Map<String, Any> = emptyMap()
    ) : AppDestination(extraMap) {
        companion object {
            const val REQUEST_CODE = 1003
        }
    }
}
