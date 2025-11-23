package com.uniandes.medisupply.presentation.model

import android.os.Parcelable
import com.uniandes.medisupply.domain.model.Client
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClientUI(
    val name: String,
    val address: String,
    val phone: String,
    val email: String,
    val contactName: String
) : Parcelable

fun Client.toUI() = ClientUI(
    name = name,
    address = address,
    phone = contactInfo.phone,
    email = contactInfo.email,
    contactName = contactInfo.name
)
