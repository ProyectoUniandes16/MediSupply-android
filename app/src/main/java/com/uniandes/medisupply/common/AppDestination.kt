package com.uniandes.medisupply.common

sealed class
AppDestination(val extras: Map<String, Any> = emptyMap()) {
    class HomeClient(extraMap: Map<String, Any> = emptyMap()) : AppDestination(extraMap)
    data object NewClient : AppDestination() {
        const val REQUEST_CODE = 1001
    }
    data object NewOrder : AppDestination() {
        const val REQUEST_CODE = 1002
    }
}
