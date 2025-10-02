package com.uniandes.medisupply.domain.repository

import com.uniandes.medisupply.data.remote.LoginService
import com.uniandes.medisupply.data.remote.model.LoginRequest
import com.uniandes.medisupply.domain.model.User

interface UserRepository {
    suspend fun login(email: String, password: String): Result<User>
}

class UserRepositoryImpl(
    private val loginService: LoginService
): UserRepository  {
    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val login = loginService.login(
                LoginRequest(email, password)
            )
            Result.success(
                User(
                    login.id,
                    login.name,
                    login.email)
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
