package com.uniandes.medisupply.viewModel.order

import com.uniandes.medisupply.common.AppDestination
import com.uniandes.medisupply.common.NavigationProvider
import com.uniandes.medisupply.presentation.viewmodel.order.OrderListViewModel
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class OrderListViewModelTest {
    private lateinit var viewModel: OrderListViewModel
    private val navigationProvider: NavigationProvider = mockk(relaxed = true)

    @Before
    fun setup() {
        viewModel = OrderListViewModel(navigationProvider)
    }

    @Test
    fun `on New Order clicked, navigation is requested`() {
        viewModel.onEvent(OrderListViewModel.UserEvent.OnNewOrderClicked)

        verify { navigationProvider.requestDestination(
            AppDestination.NewOrder,
            AppDestination.NewOrder.REQUEST_CODE)
        }
    }
}
