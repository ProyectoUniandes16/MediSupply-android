package com.uniandes.medisupply.data.remote.service

import com.uniandes.medisupply.data.remote.model.common.DataResponse
import com.uniandes.medisupply.data.remote.model.order.OrderResponse
import com.uniandes.medisupply.data.remote.model.order.PlaceOrderRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderService {
    @POST("/movil-producto/pedido")
    suspend fun placeOrder(@Body request: PlaceOrderRequest)

    @GET("/movil-producto/pedido")
    suspend fun getOrders(): DataResponse<List<OrderResponse>>

    @GET("/movil-producto/pedido/{id}")
    suspend fun getOrderById(@Path("id") id: Int): DataResponse<OrderResponse>
}
