package com.uniandes.medisupply.domain.repository

import com.uniandes.medisupply.common.resultOrError
import com.uniandes.medisupply.data.remote.service.OrderService
import com.uniandes.medisupply.domain.model.Order
import com.uniandes.medisupply.domain.model.toDataModel

interface OrderRepository {
    suspend fun placeOrder(order: Order): Result<Unit>
}

class OrderRepositoryImpl(
    private val service: OrderService
): OrderRepository {

    override suspend fun placeOrder(order: Order): Result<Unit> {
        return resultOrError {
            service.placeOrder(order.toDataModel())
        }
    }
}