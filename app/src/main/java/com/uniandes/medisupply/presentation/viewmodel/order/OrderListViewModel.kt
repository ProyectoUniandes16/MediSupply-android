package com.uniandes.medisupply.presentation.viewmodel.order

import androidx.lifecycle.ViewModel
import com.uniandes.medisupply.common.AppDestination
import com.uniandes.medisupply.common.NavigationProvider

class OrderListViewModel(
    private val navigationProvider: NavigationProvider
) : ViewModel() {

    fun onEvent(event: UserEvent) {
        when (event) {
            is UserEvent.OnNewOrderClicked -> {
                navigationProvider.requestDestination(
                    AppDestination.NewOrder,
                    requestResultCode = AppDestination.NewOrder.REQUEST_CODE
                )
            }
        }
    }

    sealed class UserEvent {
        data object OnNewOrderClicked : UserEvent()
    }
}
