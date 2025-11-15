package com.uniandes.medisupply.repository

import com.uniandes.medisupply.data.remote.model.common.DataResponse
import com.uniandes.medisupply.data.remote.service.OrderService
import com.uniandes.medisupply.domain.model.Order
import com.uniandes.medisupply.domain.model.toDataModel
import com.uniandes.medisupply.domain.repository.OrderRepositoryImpl
import com.uniandes.medisupply.model.TEST_ORDER_RESPONSE
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OrderResponseRepositoryTest {

    private val orderService: OrderService = mockk()
    private val orderRepository = OrderRepositoryImpl(orderService)

    @Test
    fun `place order should call orderService and return success result WHEN order is placed`() = runTest {
        // GIVEN
        val order = ORDER
        coEvery { orderService.placeOrder(order.toDataModel()) } returns Unit
        // WHEN
       val result = orderRepository.placeOrder(order)

        // THEN
        assert(result.isSuccess)
        coVerify(exactly = 1) {
            orderService.placeOrder(order.toDataModel())
        }
    }

    @Test
    fun `place order should call orderService and throw exception`() = runTest {
        // GIVEN
        val order = ORDER
        val exceptionMessage = "Network Error"
        coEvery { orderService.placeOrder(order.toDataModel()) } throws Exception(exceptionMessage)
        // WHEN
        val result = orderRepository.placeOrder(order)

        // THEN
        assertFalse(result.isSuccess)
        assertTrue(result.isFailure)
        assertEquals(exceptionMessage, result.exceptionOrNull()?.message)
        coVerify(exactly = 1) {
            orderService.placeOrder(order.toDataModel())
        }
    }

    @Test
    fun `get orders should call orderService and return order list WHEN service returns data`() = runTest {
        // GIVEN
        val orderList = listOf(TEST_ORDER_RESPONSE)
        coEvery { orderService.getOrders() } returns DataResponse(orderList)

        // WHEN
        val result = orderRepository.getOrders()

        // THEN
        assertTrue(result.isSuccess)
        assertEquals(orderList.size, result.getOrNull()?.size)
        coVerify(exactly = 1) {
            orderService.getOrders()
        }
    }

    @Test
    fun `get orders should call orderService and throw exception WHEN service fails`() = runTest {
        // GIVEN
        val exceptionMessage = "Network Error"
        coEvery { orderService.getOrders() } throws Exception(exceptionMessage)
        // WHEN
        val result = orderRepository.getOrders()
        // THEN
        assertFalse(result.isSuccess)
        assertTrue(result.isFailure)
        assertEquals(exceptionMessage, result.exceptionOrNull()?.message)
        coVerify(exactly = 1) {
            orderService.getOrders()
        }
    }

    companion object {
        private val ORDER = Order(
            clientId = 1,
            products = emptyList(),
            total = 100.0
        )
    }
}
