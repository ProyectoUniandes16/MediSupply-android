package com.uniandes.medisupply.repository
import com.uniandes.medisupply.data.remote.model.client.NewClientRequest
import org.junit.Test
import kotlin.test.assertEquals

class NewClientRequestTest {
    @Test
    fun `properties are accessible and return expected values`() {
        val req = NewClientRequest(
            name = "Name",
            type = "Type",
            country = "Country",
            address = "Addr",
            nit = "123",
            companyEmail = "c@e.com",
            contactName = "Contact",
            contactPosition = "Position",
            contactPhone = "555",
            contactEmail = "contact@e.com"
        )

        assertEquals("Name", req.name)
        assertEquals("Type", req.type)
        assertEquals("Country", req.country)
        assertEquals("Addr", req.address)
        assertEquals("123", req.nit)
        assertEquals("c@e.com", req.companyEmail)
        assertEquals("Contact", req.contactName)
        assertEquals("Position", req.contactPosition)
        assertEquals("555", req.contactPhone)
        assertEquals("contact@e.com", req.contactEmail)
    }
}