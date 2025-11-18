package com.uniandes.medisupply.presentation.containers

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.uniandes.medisupply.common.BaseActivity
import com.uniandes.medisupply.common.InternalNavigator
import com.uniandes.medisupply.presentation.navigation.navhost.LobbyNavHost
import com.uniandes.medisupply.presentation.ui.theme.MediSupplyTheme
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity : BaseActivity(), KoinComponent {
    private val internalNavigator: InternalNavigator by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            internalNavigator.init(navController, this)
            MediSupplyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LobbyNavHost(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        internalNavigator.clear()
    }
}
