package com.uniandes.medisupply.di

import com.uniandes.medisupply.common.NavigationProvider
import com.uniandes.medisupply.common.NavigationProviderImpl
import org.koin.dsl.module

val commonModule = module {
    single<NavigationProvider> { NavigationProviderImpl() }
}