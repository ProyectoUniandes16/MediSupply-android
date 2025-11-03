package com.uniandes.medisupply.repository

import com.uniandes.medisupply.data.remote.model.client.NewClientRequest
import com.uniandes.medisupply.data.remote.model.common.DataResponse
import com.uniandes.medisupply.data.remote.service.ClientService
import com.uniandes.medisupply.domain.repository.ClientRepositoryImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ClientRepositoryTest {

    private val clientService: ClientService = mockk()
    private val clientRepository = ClientRepositoryImpl(clientService)

    companion object {

        const val name = "name"
        const val lastName = "lastName"
        const val email = "test@test.com"
        const val type = "type"
        const val address = "address"
        const val nit = "1234567890"
        const val country = "country"
        const val contactName = "contactName"
        const val contactPosition = "contactPosition"
        const val contactPhone = "1234567890"
        const val contactEmail = "contactEmail"
    }

    @Test
    fun `test add new client success`(): Unit = runTest {
        // Given
        val clientResponse = DataResponse(Unit)
        val newClientRequest = NewClientRequest(
            name = name,
            type = type,
            contactName = contactName,
            contactPhone = contactPhone,
            companyEmail = email,
            address = address,
            contactPosition = contactPosition,
            nit = nit,
            country = country,
            contactEmail = contactEmail
        )
        coEvery { clientService.addClient(newClientRequest) } returns clientResponse

        // When
        val result: Result<Boolean> = clientRepository.addClient(
            name,
            type,
            nit,
            address,
            country,
            contactName,
            contactPosition,
            contactPhone,
            contactEmail,
            email
        )

        // Then
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        assertEquals(true, result.getOrNull())
    }

    @Test
    fun `test add new client fails`(): Unit = runTest {
        // Given
        val newClientRequest = NewClientRequest(
            name = name,
            type = type,
            contactName = contactName,
            contactPhone = contactPhone,
            companyEmail = email,
            address = address,
            contactPosition = contactPosition,
            nit = nit,
            country = country,
            contactEmail = contactEmail
        )
        coEvery { clientService.addClient(newClientRequest) } throws Exception("Error adding client")

        // When
        val result: Result<Boolean> = clientRepository.addClient(
            name,
            type,
            nit,
            address,
            country,
            contactName,
            contactPosition,
            contactPhone,
            contactEmail,
            email
        )

        // Then
        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }
}
