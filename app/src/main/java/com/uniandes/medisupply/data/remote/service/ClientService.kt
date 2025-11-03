package com.uniandes.medisupply.data.remote.service

import com.uniandes.medisupply.data.remote.model.client.ClientResponse
import com.uniandes.medisupply.data.remote.model.client.NewClientRequest
import com.uniandes.medisupply.data.remote.model.common.DataResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ClientService {
    @POST("/movil/cliente")
    suspend fun addClient(@Body request: NewClientRequest): DataResponse<Unit>

    @GET("/movil/cliente")
    suspend fun getClients(): DataResponse<List<ClientResponse>>
}
