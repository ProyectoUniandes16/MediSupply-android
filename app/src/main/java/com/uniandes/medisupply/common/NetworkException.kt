package com.uniandes.medisupply.common

class NetworkException(
    val code: String? = null,
    override val message: String?
) : Exception(message) {
    override fun toString(): String {
        return "NetworkException(code=$code, message=$message)"
    }
}
