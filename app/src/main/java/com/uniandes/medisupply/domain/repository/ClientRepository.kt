package com.uniandes.medisupply.domain.repository

import com.uniandes.medisupply.common.resultOrError
import com.uniandes.medisupply.data.remote.model.client.NewClientRequest
import com.uniandes.medisupply.data.remote.service.ClientService
import com.uniandes.medisupply.domain.model.Client
import com.uniandes.medisupply.domain.model.ClientContactInfo

interface ClientRepository {
    suspend fun addClient(
        name: String,
        type: String,
        nit: String,
        address: String,
        country: String,
        contactName: String,
        contactPosition: String,
        contactPhone: String,
        contactEmail: String,
        companyEmail: String,
    ): Result<Boolean>

    suspend fun getClients(): Result<List<Client>>
}

class ClientRepositoryImpl(
    private val clientService: ClientService
) : ClientRepository {
    override suspend fun addClient(
        name: String,
        type: String,
        nit: String,
        address: String,
        country: String,
        contactName: String,
        contactPosition: String,
        contactPhone: String,
        contactEmail: String,
        companyEmail: String,
    ): Result<Boolean> {
        return try {
            clientService.addClient(
                NewClientRequest(
                    name = name,
                    type = type,
                    country = country,
                    address = address,
                    nit = nit,
                    contactName = contactName,
                    contactPosition = contactPosition,
                    contactPhone = contactPhone,
                    contactEmail = contactEmail,
                    companyEmail = companyEmail
                )
            )
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(Exception(e))
        }
    }

    override suspend fun getClients(): Result<List<Client>> {
        return resultOrError {
            clientService.getClients().data.map { clientResponse ->
                Client(
                    id = clientResponse.id,
                    name = clientResponse.name,
                    address = clientResponse.address,
                    email = clientResponse.email,
                    contactInfo = ClientContactInfo(
                        name = clientResponse.contact.name,
                        phone = clientResponse.contact.phone,
                        email = clientResponse.contact.email,
                        position = clientResponse.contact.position
                    )
                )
            }
        }
    }
}
