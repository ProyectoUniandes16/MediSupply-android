package com.uniandes.medisupply.di

import com.uniandes.medisupply.common.NetworkModule.createAuthService
import com.uniandes.medisupply.data.remote.service.ClientService
import com.uniandes.medisupply.domain.repository.ClientRepository
import com.uniandes.medisupply.domain.repository.ClientRepositoryImpl
import com.uniandes.medisupply.presentation.viewmodel.ClientListViewModel
import com.uniandes.medisupply.presentation.viewmodel.NewClientViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val vendorModule = module {
    single<ClientService> { createAuthService(ClientService::class.java) }
    single<ClientRepository> { ClientRepositoryImpl(get()) }
    viewModel {
        ClientListViewModel(
            navigationProvider = get()
        )
    }
    viewModel {
        NewClientViewModel(
            get(),
            get()
        )
    }
}