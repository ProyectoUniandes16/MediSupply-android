package com.uniandes.medisupply.presentation.navigation.navhost

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.uniandes.medisupply.presentation.navigation.Destination
import com.uniandes.medisupply.presentation.ui.feature.client.NewClientScreen
import com.uniandes.medisupply.presentation.ui.feature.lobby.LoginScreen

@Composable
fun LobbyNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(navController = navController, startDestination = Destination.Login) {
        composable<Destination.Login> {
            LoginScreen()
        }
        composable<Destination.NewClient> {
            NewClientScreen()
        }
    }
}
