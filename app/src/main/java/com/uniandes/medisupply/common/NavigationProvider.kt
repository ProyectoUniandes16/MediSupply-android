package com.uniandes.medisupply.common

import android.app.Activity
import android.util.Log
import com.uniandes.medisupply.HomeClientActivity

interface NavigationProvider {
    fun init(activity: Activity)
    fun requestDestination(appDestination: AppDestination)
    fun finishCurrentDestination()
    fun tearDown()
}

class NavigationProviderImpl : NavigationProvider {
    companion object {
       private const val TAG = "NavigationProvider"
    }
    private var activity: Activity? = null

    override fun init(activity: Activity) {
        this.activity = activity
        activity
    }

    override fun requestDestination(appDestination: AppDestination) {
       activity?.let { activity ->
           val intent = when (appDestination) {
               is AppDestination.HomeClient -> HomeClientActivity.createIntent(activity)
           }.apply {
               putExtras(appDestination.extras)
           }
           activity.startActivity(intent)
       } ?: run { Log.w(TAG, "Activity is null") }
    }

    override fun finishCurrentDestination() {
        activity?.finish()
    }

    override fun tearDown() {
        activity = null
    }
}