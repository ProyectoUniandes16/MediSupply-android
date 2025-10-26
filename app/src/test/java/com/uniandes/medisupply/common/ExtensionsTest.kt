package com.uniandes.medisupply.common

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ExtensionsTest {

    @Test
    fun `invalid email returns false`() {
        val email = "invalidemail.com"
        assertFalse(email.isValidEmail())
    }

    @Test
    fun `invalid email returns true`() {
        val email = "invalid@email.com"
        assertTrue(email.isValidEmail())
    }

    @Test
    fun `invalid phone returns false`() {
        val phone = "phone123"
        assertFalse(phone.isValidPhone())
    }

    @Test
    fun `invalid phone returns true`() {
        val phone = "1111111111"
        assertTrue(phone.isValidPhone())
    }
}