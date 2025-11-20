package com.uniandes.medisupply.domain.model

data class Visit(
    val status: String,
    val visitDate: String,
    val client: Client
)
