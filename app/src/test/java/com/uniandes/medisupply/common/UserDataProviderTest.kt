package com.uniandes.medisupply.common

import com.uniandes.medisupply.domain.model.User
import com.uniandes.medisupply.domain.model.UserRole
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class UserDataProviderTest {

    private lateinit var userDataProvider: UserDataProvider
    private val userPreferences: UserPreferences = mockk(relaxed = true)

    @Before
    fun setUp() {
        userDataProvider = UserDataProviderImpl(userPreferences)
    }

    @Test
    fun `setUserData SHOULD save  user preferences`() {
        // Given
        val user = User(
            id = 1,
            name = "John Doe",
            email = "email@email.com",
            role = UserRole.VENDOR
        )
        val token = "mocked_access_token"

        // When
        userDataProvider.setUserData(token, user)

        // Then
        verify {
            userPreferences.setAccessToken(token)
            userPreferences.setRole(user.role.displayName)
            userPreferences.setLoggedIn(true)
        }
    }

    @Test
    fun `getAccessToken SHOULD return access token from user preferences`() {
        // Given
        val expectedToken = "mocked_access_token"
        every { userPreferences.getAccessToken() } returns expectedToken
        // When
        val token = userDataProvider.getAccessToken()

        // Then
        assertEquals(expectedToken, token)
    }

    @Test
    fun `getName SHOULD return name from user preferences`() {
        // Given
        val expectedName = "John Doe"
        every { userPreferences.getName() } returns expectedName
        // When
        val name = userDataProvider.getName()

        // Then
        assertEquals(expectedName, name)
    }

    @Test
    fun `getEmail SHOULD return email from user preferences`() {
        // Given
        val expectedEmail = "email"
        every { userPreferences.getEmail() } returns expectedEmail
        // When
        val email = userDataProvider.getEmail()
        // Then
        assertEquals(expectedEmail, email)
    }

    @Test
    fun `getPhone SHOULD return phone from user preferences`() {
        // Given
        val expectedPhone = "123456789"
        every { userPreferences.getPhone() } returns expectedPhone
        // When
        val phone = userDataProvider.getPhone()
        // Then
        assertEquals(expectedPhone, phone)
    }

    @Test
    fun `isLoggedIn SHOULD return value from user preferences`() {
        // Given
        every { userPreferences.isLoggedIn() } returns false
        // When
        val isLoggedIn = userDataProvider.isUserLoggedIn()

        // Then
        assertFalse(isLoggedIn)
    }
}
