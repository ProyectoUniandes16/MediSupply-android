package com.uniandes.medisupply.data.remote.service

import com.uniandes.medisupply.data.remote.model.client.NewClientRequest
import com.uniandes.medisupply.data.remote.model.common.DataResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ClientService {
    @POST("/cliente")
    suspend fun addClient(@Body request: NewClientRequest): DataResponse<Unit>
}
