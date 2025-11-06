package com.uniandes.medisupply.companion

import com.uniandes.medisupply.domain.model.Client
import com.uniandes.medisupply.domain.model.ClientContactInfo

val CONTACT_INFO = ClientContactInfo(
    phone = "1234567890",
    email = "coreo@corre.com",
    name = "contact name",
    position = "Manager"
)
val CLIENT = Client(
    id = 1,
    name = "John Doe",
    email = "correo@correo.com",
    address = "123 Main St",
    contactInfo = CONTACT_INFO)

val CLIENT_LIST = List(10) { index ->
    CLIENT.copy(id = index + 1, name = "Client $index")
}
