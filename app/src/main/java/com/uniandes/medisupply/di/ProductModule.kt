package com.uniandes.medisupply.di

import com.uniandes.medisupply.common.NetworkModule.createAuthService
import com.uniandes.medisupply.data.remote.service.ProductService
import com.uniandes.medisupply.domain.repository.ProductRepository
import com.uniandes.medisupply.domain.repository.ProductRepositoryImpl
import org.koin.dsl.module

val productModule = module {
    single<ProductService> {
        createAuthService(ProductService::class.java)
    }
    single<ProductRepository> { ProductRepositoryImpl(get()) }
}