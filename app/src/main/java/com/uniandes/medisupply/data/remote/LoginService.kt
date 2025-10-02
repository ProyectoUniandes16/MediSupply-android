package com.uniandes.medisupply.data.remote

import com.uniandes.medisupply.data.remote.model.LoginRequest
import com.uniandes.medisupply.data.remote.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {

    @POST("/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}