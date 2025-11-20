package com.uniandes.medisupply.domain.model

import android.os.Parcelable
import com.uniandes.medisupply.data.remote.model.client.ClientResponse
import kotlinx.parcelize.Parcelize

enum class ClientType(val displayName: String) {
    HOSPITAL("Hospital"),
    CLINIC("Clinica"),
    LABORATORY("Laboratorio"),
    DISTRIBUTOR("Distribuidor"),
    EPS_EAPB("EPS/EAPB"),
    IPS("IPS"),
    OTHER("Otro")
}

enum class Zone(val displayName: String) {
    CIUDAD_DE_MEXICO("Ciudad de México"),
    BOGOTA("Bogotá"),
    QUITO("Quito"),
    LIMA("Lima")
}

@Parcelize
data class Client(
    val id: Int,
    val name: String,
    val address: String,
    val email: String,
    val contactInfo: ClientContactInfo
) : Parcelable

@Parcelize
data class ClientContactInfo(
    val name: String,
    val phone: String,
    val email: String,
    val position: String
) : Parcelable

fun ClientResponse.toDomain() = Client(
    id = this.id,
    name = this.name,
    address = this.address,
    email = this.email,
    contactInfo = ClientContactInfo(
        name = this.contact.name,
        phone = this.contact.phone,
        email = this.contact.email,
        position = this.contact.position
    )
)
