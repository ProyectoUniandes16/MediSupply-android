package com.uniandes.medisupply.di

import com.uniandes.medisupply.common.NetworkModule.createService
import com.uniandes.medisupply.data.remote.LoginService
import com.uniandes.medisupply.domain.repository.UserRepository
import com.uniandes.medisupply.domain.repository.UserRepositoryImpl
import com.uniandes.medisupply.presentation.viewmodel.LoginViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val userModule = module {
    single<LoginService> { createService(LoginService::class.java) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    viewModel {
        LoginViewModel(
            navigationProvider = get(),
            userRepository = get()
        )
    }
}