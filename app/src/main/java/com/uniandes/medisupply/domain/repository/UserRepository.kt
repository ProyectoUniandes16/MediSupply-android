package com.uniandes.medisupply.domain.repository

import com.uniandes.medisupply.data.remote.service.LoginService
import com.uniandes.medisupply.data.remote.model.LoginRequest
import com.uniandes.medisupply.domain.model.User

interface UserRepository {
    suspend fun login(email: String, password: String): Result<Pair<User, String>>
}

class UserRepositoryImpl(
    private val loginService: LoginService
): UserRepository  {
    override suspend fun login(email: String, password: String): Result<Pair<User, String>> {
        return try {
            val response = loginService.login(
                LoginRequest(email, password)
            )
            val user = response.data.user
            val token = response.data.accessToken
            val pair = Pair(
                User(
                    id = user.id,
                    name = user.name + " " + user.lastName,
                    email = user.email
                ),
                token
            )
            Result.success(pair)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
