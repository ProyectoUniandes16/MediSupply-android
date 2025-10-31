package com.uniandes.medisupply.data.remote.service

import com.uniandes.medisupply.data.remote.model.order.PlaceOrderRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface OrderService {
    @POST("/movil/pedido")
    suspend fun placeOrder(@Body request: PlaceOrderRequest)
}
