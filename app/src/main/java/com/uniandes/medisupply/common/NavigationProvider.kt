package com.uniandes.medisupply.common

import android.app.Activity
import android.os.Parcelable
import android.util.Log
import com.uniandes.medisupply.HomeClientActivity
import kotlinx.android.parcel.Parcelize

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
               appDestination.extras.forEach {
                   if (it.value is Parcelable) {
                       putExtra(it.key, it.value as Parcelable)
                   } else {
                       Log.w(TAG, "Extra ${it.key} is not a parcelable")
                   }
               }
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