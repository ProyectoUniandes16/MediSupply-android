package com.uniandes.medisupply.common

import androidx.activity.ComponentActivity
import org.koin.android.ext.android.inject

open class BaseActivity : ComponentActivity() {
    private val navigationProvider: NavigationProvider by inject()
    override fun onResume() {
        super.onResume()
        navigationProvider.init(this)
    }

    override fun onPause() {
        super.onPause()
        navigationProvider.tearDown()
    }
}
