package com.uniandes.medisupply.data.remote.service

import com.uniandes.medisupply.data.remote.model.user.LoginRequest
import com.uniandes.medisupply.data.remote.model.client.NewClientRequest
import com.uniandes.medisupply.data.remote.model.common.DataResponse
import com.uniandes.medisupply.data.remote.model.user.UserWrapperResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("/movil/auth/login")
    suspend fun login(@Body request: LoginRequest): DataResponse<UserWrapperResponse>
    @POST("/movil/auth/signup/cliente")
    suspend fun signUpClient(@Body request: NewClientRequest): DataResponse<UserWrapperResponse>
}
