package com.uniandes.medisupply.repository

import com.uniandes.medisupply.data.remote.LoginService
import com.uniandes.medisupply.data.remote.model.LoginRequest
import com.uniandes.medisupply.data.remote.model.LoginResponse
import com.uniandes.medisupply.domain.model.User
import com.uniandes.medisupply.domain.repository.UserRepositoryImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class UserRepositoryTest {

    private val loginService: LoginService = mockk()
    private val userRepository = UserRepositoryImpl(loginService)

    @Test
    fun `test login success`(): Unit = runBlocking {
        // Given
        val email = "email"
        val password = "password"

        val loginResponse = LoginResponse(
            10,
            "name",
            email
        )
        val bodyRequest = LoginRequest(
            email,
            password
        )
        coEvery { loginService.login(bodyRequest) } returns loginResponse

        // When
        val result: Result<User> = userRepository.login(email, password)

        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        assertEquals(result.getOrNull()!!.email, loginResponse.email)
    }

}