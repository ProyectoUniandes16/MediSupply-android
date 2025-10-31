package com.uniandes.medisupply.di

import com.uniandes.medisupply.common.NetworkModule.createAuthService
import com.uniandes.medisupply.data.remote.service.OrderService
import com.uniandes.medisupply.domain.repository.OrderRepository
import com.uniandes.medisupply.domain.repository.OrderRepositoryImpl
import com.uniandes.medisupply.presentation.viewmodel.order.CreateOrderViewModel
import com.uniandes.medisupply.presentation.viewmodel.order.ClientListOrderViewModel
import com.uniandes.medisupply.presentation.viewmodel.order.OrderListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val orderModule = module {
    single<OrderService> {
        createAuthService(OrderService::class.java)
    }
    single<OrderRepository> { OrderRepositoryImpl(get()) }
    viewModel {
        OrderListViewModel(
            navigationProvider = get()
        )
    }
    viewModel {
        ClientListOrderViewModel(
            get(),
            get()
        )
    }
    viewModel {
        CreateOrderViewModel(
            get(),
            get(),
            get()
        )
    }
}
