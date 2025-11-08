package com.uniandes.medisupply.presentation.navigation.navhost

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.uniandes.medisupply.presentation.navigation.ProductDestination
import com.uniandes.medisupply.presentation.ui.feature.product.ProductDetailScreen
import com.uniandes.medisupply.presentation.ui.feature.product.ProductListScreen

@Composable
fun ProductNavHost(
    modifier: Modifier = Modifier,
    navHostController: NavHostController
) {
    NavHost(navController = navHostController, startDestination = ProductDestination.ProductList) {
        composable<ProductDestination.ProductList> {
            ProductListScreen(modifier = modifier)
        }
        composable<ProductDestination.ProductDetail> {
            ProductDetailScreen(modifier = modifier)
        }
    }
}
