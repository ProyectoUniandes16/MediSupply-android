package com.uniandes.medisupply.domain.model

enum class ClientType(val displayName: String) {
    HOSPITAL("Hospital"),
    CLINIC("Clinica"),
    LABORATORY("Laboratorio"),
    DISTRIBUTOR("Distribuidor"),
    EPS_EAPB("EPS/EAPB"),
    IPS("IPS"),
    OTHER("Otro")
}

enum class Country(val displayName: String) {
    COLOMBIA("Colombia"),
    MEXICO("Méxcio"),
    ARGENTINA("Argentina"),
    PERU("Perú"),
    ECUADOR("Ecuador"),
}

data class Client(
    val id: Int,
    val name: String,
    val address: String,
    val email: String,
    val contactInfo: ClientContactInfo
)

data class ClientContactInfo(
    val name: String,
    val phone: String,
    val email: String,
    val position: String
)
