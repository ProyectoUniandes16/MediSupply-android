package com.uniandes.medisupply.common

import android.app.Activity
import android.os.Parcelable
import android.util.Log
import com.uniandes.medisupply.presentation.containers.ComposableActivity
import com.uniandes.medisupply.presentation.containers.HomeClientActivity
import com.uniandes.medisupply.presentation.containers.NewClientActivity
import com.uniandes.medisupply.presentation.containers.NewOrderActivity

interface NavigationProvider {
    fun init(activity: Activity)
    fun requestDestination(appDestination: AppDestination, requestResultCode: Int? = null)
    fun finishCurrentDestination(extras: Map<String, Any> = emptyMap(), success: Boolean = false)
    fun tearDown()
}

class NavigationProviderImpl : NavigationProvider {
    companion object {
       private const val TAG = "NavigationProvider"
    }
    private var activity: Activity? = null

    override fun init(activity: Activity) {
        this.activity = activity
    }

    override fun requestDestination(appDestination: AppDestination, requestResultCode: Int?) {
       activity?.let { activity ->
           val intent = when (appDestination) {
               is AppDestination.HomeClient -> HomeClientActivity.createIntent(activity)
               is AppDestination.NewClient -> {
                   NewClientActivity.createIntent(activity)
               }
               is AppDestination.NewOrder -> {
                    NewOrderActivity.createIntent(activity)
               }
               is AppDestination.ComposableDestination -> {
                   ComposableActivity.createIntent(
                       activity,
                       appDestination.flow
                   )
               }
               else -> {
                   Log.w(TAG, "Unknown destination: $appDestination")
                   return
               }
           }.apply {
               appDestination.extras.forEach {
                   when (it.value) {
                       is Int -> putExtra(it.key, it.value as Int)
                       is String -> putExtra(it.key, it.value as String)
                       is Boolean -> putExtra(it.key, it.value as Boolean)
                       is Float -> putExtra(it.key, it.value as Float)
                       is Double -> putExtra(it.key, it.value as Double)
                       is Long -> putExtra(it.key, it.value as Long)
                       is Char -> putExtra(it.key, it.value as Char)
                       is Short -> putExtra(it.key, it.value as Short)
                       is Byte -> putExtra(it.key, it.value as Byte)
                       else -> {
                           if (it.value is Parcelable) {
                               putExtra(it.key, it.value as Parcelable)
                           } else {
                               Log.w(TAG, "Extra ${it.key} is not a parcelable")
                           }
                       }
                   }
               }
           }
           if (requestResultCode != null) {
               activity.startActivityForResult(intent, requestResultCode)
           } else {
               activity.startActivity(intent)
           }
       } ?: run { Log.w(TAG, "Activity is null") }
    }

    override fun finishCurrentDestination(extras: Map<String, Any>, success: Boolean) {
        val intent = activity?.intent
        if (extras.isNotEmpty()) {
            extras.forEach {
                if (it.value is Parcelable) {
                    intent?.putExtra(it.key, it.value as Parcelable)
                } else {
                    Log.w(TAG, "Extra ${it.key} is not a parcelable")
                }
            }
        }
        activity?.setResult(
            if (success) Activity.RESULT_OK else Activity.RESULT_CANCELED,
                intent
        )
        activity?.finish()
    }

    override fun tearDown() {
        activity = null
    }
}
