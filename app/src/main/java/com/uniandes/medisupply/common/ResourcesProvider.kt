package com.uniandes.medisupply.common

import android.content.Context

interface ResourcesProvider {
    fun getString(resId: Int, vararg args: Any): String
    fun getStringArray(resId: Int): Array<String>
}

class ResourcesProviderImpl(private val context: Context) : ResourcesProvider {
    override fun getString(resId: Int, vararg args: Any): String {
        return context.getString(resId, *args)
    }

    override fun getStringArray(resId: Int): Array<String> {
        return context.resources.getStringArray(resId)
    }
}
