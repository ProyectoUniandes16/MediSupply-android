package com.uniandes.medisupply.domain.repository

import com.uniandes.medisupply.R
import com.uniandes.medisupply.data.remote.model.client.NewClientRequest
import com.uniandes.medisupply.data.remote.service.ClientService

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
}
