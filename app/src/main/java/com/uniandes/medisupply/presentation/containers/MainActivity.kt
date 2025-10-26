package com.uniandes.medisupply.presentation.containers

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.uniandes.medisupply.common.BaseActivity
import com.uniandes.medisupply.presentation.navigation.navhost.LobbyNavHost
import com.uniandes.medisupply.presentation.ui.theme.MediSupplyTheme

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MediSupplyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainAppContent(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainAppContent(modifier: Modifier = Modifier) {
    LobbyNavHost(modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MediSupplyTheme {
        MainAppContent()
    }
}
