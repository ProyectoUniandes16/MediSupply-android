package com.uniandes.medisupply.common

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    private const val BASE_URL = "https://web:3000"
    //private const val BASE_URL = "http://web:3000/"
    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    fun <T> createService(serviceClass: Class<T>): T = getRetrofit().create(serviceClass)
}