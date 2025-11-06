package com.uniandes.medisupply.presentation.containers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.uniandes.medisupply.common.BaseActivity
import com.uniandes.medisupply.common.InternalNavigator
import com.uniandes.medisupply.domain.model.Client
import com.uniandes.medisupply.presentation.navigation.Destination
import com.uniandes.medisupply.presentation.ui.feature.order.ClientListOrderScreen
import com.uniandes.medisupply.presentation.ui.feature.order.ClientOrderScreen
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NewOrderActivity : BaseActivity(), KoinComponent {

    private val internalNavigator: InternalNavigator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val client: Client? = intent.getParcelableExtra(Destination.CreateOrder.CLIENT, Client::class.java)
        setContent {
            val navController = rememberNavController()
            internalNavigator.init(navController, this)
            NewOrderNavHost(
                navController = navController,
                startDestination = if (client != null) {
                    internalNavigator.addParams(mapOf(Destination.CreateOrder.CLIENT to client))
                    Destination.CreateOrder
                } else {
                    Destination.ClientListOrder
                }
            )
        }
    }

    @Composable
    fun NewOrderNavHost(
        modifier: Modifier = Modifier,
        navController: NavHostController,
        startDestination: Destination = Destination.ClientListOrder
    ) {
        NavHost(navController = navController, startDestination = startDestination) {
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
