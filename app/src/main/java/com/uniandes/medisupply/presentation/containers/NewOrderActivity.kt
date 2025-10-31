package com.uniandes.medisupply.presentation.containers

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
import com.uniandes.medisupply.common.InternalNavigator
import com.uniandes.medisupply.presentation.navigation.Destination
import com.uniandes.medisupply.presentation.ui.feature.order.ClientListOrderScreen
import com.uniandes.medisupply.presentation.ui.feature.order.ClientOrderScreen
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NewOrderActivity: BaseActivity(), KoinComponent {

    private val internalNavigator: InternalNavigator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NewOrderNavHost()
        }
    }

    @Composable
    fun NewOrderNavHost(modifier: Modifier = Modifier) {
        val navController = rememberNavController()
        internalNavigator.init(navController, this)
        NavHost(navController = navController, startDestination = Destination.ClientListOrder) {
            composable<Destination.ClientListOrder> {
                ClientListOrderScreen()
            }
            composable<Destination.CreateOrder> {
                ClientOrderScreen()
            }
        }
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, NewOrderActivity::class.java)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        internalNavigator.clear()
    }
}