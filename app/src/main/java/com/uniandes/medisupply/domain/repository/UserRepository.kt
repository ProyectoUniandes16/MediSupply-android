package com.uniandes.medisupply.domain.repository

import com.uniandes.medisupply.common.resultOrError
import com.uniandes.medisupply.data.remote.service.LoginService
import com.uniandes.medisupply.data.remote.model.LoginRequest
import com.uniandes.medisupply.data.remote.model.client.NewClientRequest
import com.uniandes.medisupply.domain.model.User
import com.uniandes.medisupply.domain.model.UserRole

interface UserRepository {
    suspend fun login(email: String, password: String): Result<Pair<User, String>>
    suspend fun signUpClient(
        name: String,
        companyEmail: String,
        password: String,
        contactName: String,
        contactEmail: String,
        contactPhone: String,
        address: String,
        nit: String,
        zone: String,
        type: String,
        contactPosition: String,
    ): Result<Pair<User, String>>
}

class UserRepositoryImpl(
    private val loginService: LoginService
) : UserRepository {
    override suspend fun login(email: String, password: String): Result<Pair<User, String>> {
        return resultOrError {
            val response = loginService.login(
                LoginRequest(email, password)
            )
            val user = response.data.user
            val token = response.data.accessToken
            Pair(
                User(
                    id = user.id,
                    name = user.name + " " + user.lastName,
                    email = user.email,
                    role = UserRole.fromDisplayName(user.role) ?: throw Exception("Invalid role")
                ),
                token
            )
        }
    }

    override suspend fun signUpClient(
        name: String,
        companyEmail: String,
        password: String,
        contactName: String,
        contactEmail: String,
        contactPhone: String,
        address: String,
        nit: String,
        zone: String,
        type: String,
        contactPosition: String,
    ): Result<Pair<User, String>> {
        return resultOrError {
            val response = loginService.signUpClient(
                NewClientRequest(
                    name = name,
                    type = type,
                    country = zone,
                    address = address,
                    nit = nit,
                    companyEmail = companyEmail,
                    contactName = contactName,
                    contactPosition = contactPosition,
                    contactPhone = contactPhone,
                    contactEmail = contactEmail,
                    password = password
                )
            )
            val user = response.data.user
            val token = response.data.accessToken
            Pair(
                User(
                    id = user.id,
                    name = user.name + " " + user.lastName,
                    email = user.email,
                    role = UserRole.fromDisplayName(user.role) ?: throw Exception("Invalid role")
                ),
                token
            )
        }
    }
}
