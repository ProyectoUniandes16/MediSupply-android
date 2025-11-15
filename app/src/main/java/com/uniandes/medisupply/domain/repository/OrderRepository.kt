package com.uniandes.medisupply.domain.repository

import com.uniandes.medisupply.common.resultOrError
import com.uniandes.medisupply.data.remote.service.OrderService
import com.uniandes.medisupply.domain.model.Order
import com.uniandes.medisupply.domain.model.toDataModel
import com.uniandes.medisupply.domain.model.toDomain

interface OrderRepository {
    suspend fun placeOrder(order: Order): Result<Unit>
    suspend fun getOrders(): Result<List<Order>>
    suspend fun getOrderById(id: Int): Result<Order>
}

class OrderRepositoryImpl(
    private val service: OrderService
) : OrderRepository {

    override suspend fun placeOrder(order: Order): Result<Unit> {
        return resultOrError {
            service.placeOrder(order.toDataModel())
        }
    }

    override suspend fun getOrders(): Result<List<Order>> {
        return resultOrError {
            service.getOrders().data.map { it.toDomain() }
        }
    }

    override suspend fun getOrderById(id: Int): Result<Order> {
        return resultOrError {
            service.getOrderById(id).data.toDomain()
        }
    }
}
