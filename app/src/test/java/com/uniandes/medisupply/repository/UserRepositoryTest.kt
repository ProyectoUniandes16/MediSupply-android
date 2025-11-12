package com.uniandes.medisupply.repository

import com.uniandes.medisupply.data.remote.service.LoginService
import com.uniandes.medisupply.data.remote.model.LoginRequest
import com.uniandes.medisupply.data.remote.model.client.NewClientRequest
import com.uniandes.medisupply.data.remote.model.common.DataResponse
import com.uniandes.medisupply.data.remote.model.user.UserResponse
import com.uniandes.medisupply.data.remote.model.user.UserWrapperResponse
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

    companion object {
        const val email = "email"
        const val password = "password"
        const val name = "name"
        const val lastName = "lastName"
        const val rol = "vendor"
        const val token = "token"
        const val type = "type"
        const val country = "country"
        const val address = "address"
        const val nit = "nit"
        const val position = "position"
        const val phone = "1234567890"

        val USER_RESPONSE = UserResponse(
            10,
            name,
            lastName,
            email,
            rol,
        )

        val USER_WRAPPER_RESPONSE = UserWrapperResponse(
            USER_RESPONSE,
            token
        )
        val DATA_RESPONSE = DataResponse<UserWrapperResponse>(
            USER_WRAPPER_RESPONSE
        )
    }

    @Test
    fun `test login success`(): Unit = runBlocking {
        // Given
        val loginResponse = DATA_RESPONSE

        val bodyRequest = LoginRequest(
            email,
            password
        )
        coEvery { loginService.login(bodyRequest) } returns loginResponse

        // When
        val result: Result<Pair<User, String>> = userRepository.login(email, password)

        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        val user: User = result.getOrNull()!!.first
        assertEquals(user, result.getOrNull()!!.first)
        assertEquals(token, result.getOrNull()!!.second)
    }

    @Test
    fun `test signUpClient success`(): Unit = runBlocking {
        // Given
        val loginResponse = DATA_RESPONSE

        val bodyRequest = NewClientRequest(
            name,
            type,
            country,
            address,
            nit,
            email,
            name,
            position,
            phone,
            email,
            password
        )
        coEvery { loginService.signUpClient(bodyRequest) } returns loginResponse

        // When
        val result = userRepository.signUpClient(
            name,
            email,
            password,
            name,
            email,
            phone,
            address,
            nit,
            country,
            type,
            position
        )

        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        val user: User = result.getOrNull()!!.first
        assertEquals(user, result.getOrNull()!!.first)
        assertEquals(token, result.getOrNull()!!.second)
    }
}
