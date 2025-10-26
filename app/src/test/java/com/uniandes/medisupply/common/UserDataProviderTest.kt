package com.uniandes.medisupply.common

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
    fun `setAccessToken SHOULD call setAccessToken on user preferences`() {

        // Given
        val expectedToken = "mocked_access_token"
        // When
        val token = userDataProvider.setAccessToken(expectedToken)

        // Then
        verify { userPreferences.setAccessToken(expectedToken) }
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

    @Test
    fun `setUserLoggedIn SHOULD call setAccessToken on user preferences`() {

        // Given
        val expectedValue = true
        // When
        val token = userDataProvider.setUserLoggedIn(expectedValue)

        // Then
        verify { userPreferences.setLoggedIn(expectedValue) }
    }
}