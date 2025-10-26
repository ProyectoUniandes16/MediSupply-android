package com.uniandes.medisupply.presentation.containers

import android.app.ComponentCaller
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.uniandes.medisupply.common.BaseActivity
import com.uniandes.medisupply.presentation.navigation.Destination
import com.uniandes.medisupply.presentation.ui.feature.client.NewClientScreen

class NewClientActivity: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NewClientNavHost()
        }
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, NewClientActivity::class.java)
        }
    }
}


@Composable
private fun NewClientNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Destination.NewClient) {
        composable<Destination.NewClient> {
            NewClientScreen()
        }
    }
}