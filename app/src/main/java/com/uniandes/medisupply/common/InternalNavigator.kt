package com.uniandes.medisupply.common

import android.app.Activity
import androidx.navigation.NavController

interface InternalNavigator {
    fun stepBack()
    fun navigateTo(destination: Any, params: Map<String, Any>)
    fun init(navController: NavController, activity: Activity)
    fun clear()
    fun addParams(params: Map<String, Any>)
    fun getParam(key: String): Any
    fun requestDestination(appDestination: AppDestination, requestResultCode: Int? = null)
    fun finishCurrentDestination(extras: Map<String, Any> = emptyMap(), success: Boolean = false)
}

class InternalNavigatorImpl(
    private val navigationProvider: NavigationProvider
) : InternalNavigator {

    private var navController: NavController? = null
    private var params: MutableMap<String, Any> = mutableMapOf()
    private var activity: Activity? = null

    override fun init(navController: NavController, activity: Activity) {
        this.navController = navController
        this.activity = activity
        params = mutableMapOf()
    }

    override fun clear() {
        this.navController = null
        this.activity = null
    }

    override fun addParams(params: Map<String, Any>) {
        this.params.putAll(params)
        this.params
    }

    override fun getParam(key: String): Any {
        return this.params[key] ?: throw IllegalArgumentException("Parameter $key not found")
    }

    override fun requestDestination(appDestination: AppDestination, requestResultCode: Int?) {
        navigationProvider.requestDestination(appDestination, requestResultCode)
    }

    override fun finishCurrentDestination(extras: Map<String, Any>, success: Boolean) {
        navigationProvider.finishCurrentDestination(extras, success)
    }

    override fun stepBack() {
        navController?.let {
            if (it.previousBackStackEntry != null) {
                it.popBackStack()
            } else {
                activity?.finish()
            }
        }
    }

    override fun navigateTo(destination: Any, params: Map<String, Any>) {
        addParams(params)
        navController?.navigate(destination)
    }
}
