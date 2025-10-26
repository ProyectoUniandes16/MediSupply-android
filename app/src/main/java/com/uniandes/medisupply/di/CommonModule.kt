package com.uniandes.medisupply.di

import com.uniandes.medisupply.common.NavigationProvider
import com.uniandes.medisupply.common.NavigationProviderImpl
import com.uniandes.medisupply.common.ResourcesProvider
import com.uniandes.medisupply.common.ResourcesProviderImpl
import com.uniandes.medisupply.common.UserDataProvider
import com.uniandes.medisupply.common.UserDataProviderImpl
import com.uniandes.medisupply.common.UserPreferences
import org.koin.dsl.module

val commonModule = module {
    single<UserPreferences> { UserPreferences.getInstance(get()) }
    single<NavigationProvider> { NavigationProviderImpl() }
    single<UserDataProvider>{ UserDataProviderImpl(get()) }
    single<ResourcesProvider> { ResourcesProviderImpl(get()) }
}