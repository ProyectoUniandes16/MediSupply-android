package com.uniandes.medisupply.common

import android.content.Context

interface ResourcesProvider {
    fun getString(resId: Int, vararg args: Any): String
}

class ResourcesProviderImpl(private val context: Context) : ResourcesProvider {
    override fun getString(resId: Int, vararg args: Any): String {
        return context.getString(resId, *args)
    }
}