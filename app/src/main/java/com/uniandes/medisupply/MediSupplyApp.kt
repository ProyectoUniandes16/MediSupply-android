package com.uniandes.medisupply

import android.app.Application
import com.uniandes.medisupply.di.commonModule
import com.uniandes.medisupply.di.userModule
import com.uniandes.medisupply.di.vendorModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin


class MediSupplyApp : Application() {

    private val modules = listOf(
        commonModule,
        userModule,
        vendorModule
    )

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MediSupplyApp)
            modules(modules)
        }
    }
}