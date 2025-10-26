package com.uniandes.medisupply.data.remote.service

import com.uniandes.medisupply.data.remote.model.LoginRequest
import com.uniandes.medisupply.data.remote.model.common.DataResponse
import com.uniandes.medisupply.data.remote.model.user.UserWrapperResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): DataResponse<UserWrapperResponse>
}
