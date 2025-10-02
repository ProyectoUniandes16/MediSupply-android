package com.uniandes.medisupply

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import com.uniandes.medisupply.common.BaseActivity
import com.uniandes.medisupply.presentation.navigation.navhost.HomeClientNavHost

class HomeClientActivity: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.extras?.let {

        }
        setContent {
            HomeClientNavHost()
        }
    }

    companion object {
        const val USER_KEY = "user"
        fun createIntent(context: Context): Intent {
            return Intent(context, HomeClientActivity::class.java)
        }
    }
}