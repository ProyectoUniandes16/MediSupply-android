package com.uniandes.medisupply.repository

import com.uniandes.medisupply.data.remote.model.client.ClientResponse
import com.uniandes.medisupply.data.remote.model.client.ContactResponse
import com.uniandes.medisupply.data.remote.model.common.DataResponse
import com.uniandes.medisupply.data.remote.model.visit.VisitResponse
import com.uniandes.medisupply.data.remote.service.VendorService
import com.uniandes.medisupply.domain.repository.VendorRepositoryImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class VendorRepositoryTest {

    private val vendorService: VendorService = mockk()
    private val vendorRepository = VendorRepositoryImpl(vendorService)

    companion object {

        const val name = "name"
        const val email = "test@test.com"
    }

    @Test
    fun `get visit should return a list of visits from the vendor`(): Unit = runTest {
        // Given
        val visitList = List(10) {
            VisitResponse(
                id = it,
                visitDate = "2024-06-0${(it % 9) + 1}",
                status = when (it % 3) {
                    0 -> "finalizado"
                    1 -> "pendiente"
                    else -> "en progreso"
                },
                client = ClientResponse(
                    id = it,
                    name = "Client $it",
                    address = "Address $it",
                    email = "client@email.com",
                    contact = ContactResponse(
                        name = "Contact $it",
                        position = "Position $it",
                        phone = "Phone $it",
                        email = "Email $it"
                    ),
                    createdAt = "",
                    updatedAt = "",
                    type = "",
                    country = "",
                    taxId = ""
                )
            )
        }
        coEvery { vendorService.getVisits(any(), any()) } returns DataResponse(visitList)

        // When
        val result = vendorRepository.getVisits("", "")

        // Then
        assertTrue(result.isSuccess)
        val visits = result.getOrNull()
        assertNotNull(visits)
        assertEquals(10, visits.size)
    }

    @Test
    fun `get visits should return an error when service fails`(): Unit = runTest {
        // Given
        val error = "Service error"
        coEvery { vendorService.getVisits(any(), any()) } throws Exception(error)

        // When
        val result = vendorRepository.getVisits("", "")

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertEquals(error, exception.message)
    }
}
