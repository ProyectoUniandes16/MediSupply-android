package com.uniandes.medisupply.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.uniandes.medisupply.common.AppDestination
import com.uniandes.medisupply.common.NavigationProvider

class ClientListViewModel(
    private val navigationProvider: NavigationProvider
) : ViewModel() {

    fun onEvent(event: ClientListEvent) {
        when (event) {
            is ClientListEvent.OnNewClientClick -> {
                navigationProvider.requestDestination(
                    AppDestination.NewClient,
                    requestResultCode = AppDestination.NewClient.REQUEST_CODE
                )
            }
        }
    }

    sealed class ClientListEvent {
        data object OnNewClientClick : ClientListEvent()
    }
}
