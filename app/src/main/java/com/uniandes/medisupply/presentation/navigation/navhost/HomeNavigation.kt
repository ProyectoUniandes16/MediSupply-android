package com.uniandes.medisupply.presentation.navigation.navhost

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.uniandes.medisupply.R
import com.uniandes.medisupply.presentation.navigation.Destination
import com.uniandes.medisupply.presentation.ui.feature.home.ClientListScreen
import com.uniandes.medisupply.presentation.ui.feature.home.OrderListScreen
import kotlinx.serialization.Serializable

@Serializable
private data class BottomTabItem(
    val destination: Destination,
    val title: Int,
    val icon: Int
)

private val BOTTOM_ITEMS = listOf(
    BottomTabItem(
        destination = Destination.HomeVendor,
        title = R.string.home,
        icon = R.drawable.home
    ),
    BottomTabItem(
        destination = Destination.ClientList,
        title = R.string.clients,
        icon = R.drawable.clients
    ),
    BottomTabItem(
        destination = Destination.HomeVendor,
        title = R.string.routes,
        icon = R.drawable.commute
    ),
    BottomTabItem(
        destination = Destination.OrderList,
        title = R.string.orders,
        icon = R.drawable.orders
    )
)
@Composable
fun HomeClientNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val selectedTab = remember { mutableStateOf(BOTTOM_ITEMS[0]) }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(color = Color.Black)
                    .padding(end = 20.dp)

            ) {
            }
        },
        bottomBar = {
            BottomBar(selectedTab.value) { selectedItem ->
                selectedTab.value = selectedItem
                changeScreen(navController, selectedItem)
            }
        }) { paddingValue ->
        NavHost(
            modifier = Modifier
                .padding(paddingValue),
            navController = navController,
            startDestination = Destination.HomeVendor
        ) {
            composable<Destination.HomeVendor> {
                Text(text = "Home Client")
            }
            composable<Destination.ClientList> {
                ClientListScreen()
            }
            composable<Destination.HomeVendor> {
                Text(text = "Home Client")
            }
            composable<Destination.OrderList> {
                OrderListScreen()
            }
        }
    }
}

private fun changeScreen(navController: NavController, navItem: BottomTabItem) {
    navController.navigate(navItem.destination) {
        navController.graph.startDestinationRoute?.let { screenRoute ->
            popUpTo(screenRoute) {
                saveState = true
            }
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun BottomBar(
    selectedTab: BottomTabItem,
    onSelectedItem: (BottomTabItem) -> Unit
) {
    val items = BOTTOM_ITEMS

    Box {
        BottomAppBar(
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ) {
            for (i in items.indices) {
                val item = items[i]
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = stringResource(id = item.title),
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(id = item.title),
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                    ),
                    alwaysShowLabel = true,
                    selected = selectedTab == item,
                    onClick = {
                        onSelectedItem(item)
                    }
                )
            }
        }
    }
}
