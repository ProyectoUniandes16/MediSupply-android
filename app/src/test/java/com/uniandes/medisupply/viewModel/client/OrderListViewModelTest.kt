package com.uniandes.medisupply.viewModel.client

import com.uniandes.medisupply.domain.model.OrderStatus
import com.uniandes.medisupply.domain.repository.OrderRepository
import com.uniandes.medisupply.model.TEST_ORDER
import com.uniandes.medisupply.presentation.model.OrderStatusUI
import com.uniandes.medisupply.presentation.viewmodel.client.OrderListViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class OrderListViewModelTest {

    private lateinit var viewModel: OrderListViewModel
    private val orderRepository: OrderRepository = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = OrderListViewModel(orderRepository)
    }

    @Test
    fun `on event LoadOrders, should request the order list and set order ui list`() = runTest {
        // Given
        val orderList = ORDER_LIST
        coEvery { orderRepository.getOrders() } returns Result.success(orderList)

        // When
        viewModel.onEvent(OrderListViewModel.UserEvent.LoadOrders)
        assertTrue(viewModel.uiState.value.displayedOrders.isNotEmpty())
        assertEquals(
            orderList.filter { it.status == OrderStatus.PENDING }.size,
            viewModel.uiState.value.displayedOrders.size
        )
    }

    @Test
    fun `on event LoadOrders, should show error when request fails`() = runTest {
        // Given
        val errorMessage = "Error loading orders"
        coEvery { orderRepository.getOrders() } returns Result.failure(Exception(errorMessage))

        // When
        viewModel.onEvent(OrderListViewModel.UserEvent.LoadOrders)
        assertTrue(viewModel.uiState.value.displayedOrders.isEmpty())
        assertEquals(errorMessage, viewModel.uiState.value.error)
        assertTrue(viewModel.uiState.value.hasError)
    }

    @Test
    fun `on event OnFilterChanged, should update selected status in ui state`() = runTest {
        // Given
        val newStatus = OrderStatusUI.DELIVERED
        val orderList = ORDER_LIST
        coEvery { orderRepository.getOrders() } returns Result.success(orderList)
        // When
        viewModel.onEvent(OrderListViewModel.UserEvent.LoadOrders)

        val params = OrderStatusUI.entries.map { statusUI ->
            val expected = orderList.count { it.status?.name == statusUI.name }
            statusUI to expected
        }

        // Then - iterate parameters and assert
        params.forEach { (status, expectedCount) ->
            viewModel.onEvent(OrderListViewModel.UserEvent.OnFilterChanged(status))
            assertEquals(status, viewModel.uiState.value.selectedStatus)
            assertEquals(expectedCount, viewModel.uiState.value.displayedOrders.size)
        }
    }

    companion object {
        private val ORDER_LIST = List(10) {
            TEST_ORDER.copy(
                id = it,
                clientId = it + 1,
                total = (it + 1) * 100.0,
                totalProducts = (it + 1) * 10,
                status = when (it % 5) {
                    0 -> OrderStatus.PENDING
                    1 -> OrderStatus.DELIVERED
                    2 -> OrderStatus.IN_TRANSIT
                    3 -> OrderStatus.IN_PROGRESS
                    else -> OrderStatus.CANCELED
                }
            )
        }
    }
}
