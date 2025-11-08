package com.uniandes.medisupply.di

import com.uniandes.medisupply.common.NetworkModule.createAuthService
import com.uniandes.medisupply.data.remote.service.ProductService
import com.uniandes.medisupply.domain.repository.ProductRepository
import com.uniandes.medisupply.domain.repository.ProductRepositoryImpl
import com.uniandes.medisupply.presentation.viewmodel.product.ProductDetailViewModel
import com.uniandes.medisupply.presentation.viewmodel.product.ProductListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val productModule = module {
    single<ProductService> {
        createAuthService(ProductService::class.java)
    }
    single<ProductRepository> { ProductRepositoryImpl(get()) }
    viewModel { ProductListViewModel(get(), get(), get()) }
    viewModel { ProductDetailViewModel(get(), get()) }
}
