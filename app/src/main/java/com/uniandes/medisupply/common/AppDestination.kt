package com.uniandes.medisupply.common


sealed class AppDestination(val extras: Map<String, Any> = emptyMap()) {
    class HomeClient(extraMap: Map<String, Any> = emptyMap()) : AppDestination(extraMap)
}