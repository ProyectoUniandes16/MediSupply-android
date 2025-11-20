package com.uniandes.medisupply.di

import com.uniandes.medisupply.common.NetworkModule.createAuthService
import com.uniandes.medisupply.data.remote.service.VendorService
import com.uniandes.medisupply.domain.repository.VendorRepository
import com.uniandes.medisupply.domain.repository.VendorRepositoryImpl
import com.uniandes.medisupply.presentation.viewmodel.vendor.VisitListViewmodel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val vendorModule = module {

    single<VendorService> {
        createAuthService(VendorService::class.java)
    }

    single<VendorRepository> {
        VendorRepositoryImpl(get())
    }

    viewModel {
        VisitListViewmodel(
            vendorRepository = get()
        )
    }
}
