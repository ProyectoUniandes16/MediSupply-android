package com.uniandes.medisupply.presentation.navigation.navhost

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
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
import com.uniandes.medisupply.presentation.component.TopAppBar
import com.uniandes.medisupply.presentation.navigation.Destination
import com.uniandes.medisupply.presentation.navigation.HomeClientDestination
import com.uniandes.medisupply.presentation.ui.feature.home.ClientListScreen
import com.uniandes.medisupply.presentation.ui.feature.home.OrderListScreen
import com.uniandes.medisupply.presentation.ui.feature.home.VendorHomeScreen
import com.uniandes.medisupply.presentation.ui.feature.home.VisitVendorScreen
import kotlinx.serialization.Serializable

@Serializable
private data class BottomTabItem(
    val destination: Destination,
    val title: Int,
    val icon: Int
)

private val VENDOR_BOTTOM_ITEMS = listOf(
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
        destination = Destination.VisitList,
        title = R.string.routes,
        icon = R.drawable.commute
    ),
    BottomTabItem(
        destination = HomeClientDestination.ClientOrderList,
        title = R.string.orders,
        icon = R.drawable.orders
    )
)

private val CLIENT_BOTTOM_ITEMS = listOf(
    BottomTabItem(
        destination = HomeClientDestination.ClientOrderList,
        title = R.string.orders,
        icon = R.drawable.orders
    ),
    BottomTabItem(
        destination = HomeClientDestination.ProductList,
        title = R.string.products,
        icon = R.drawable.clients
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeClientNavHost(
    modifier: Modifier = Modifier,
    isVendor: Boolean = true,
    onLogout: () -> Unit
) {
    val tabItems = if (isVendor) VENDOR_BOTTOM_ITEMS else CLIENT_BOTTOM_ITEMS
    val navController = rememberNavController()
    val selectedTab = remember { mutableStateOf(tabItems[0]) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .height(56.dp)
                    .background(Color.White),
                navigationIcon = {
                    IconButton(
                        onClick = onLogout
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ExitToApp,
                            contentDescription = stringResource(id = R.string.exit_user),
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomBar(
                selectedTab = selectedTab.value,
                items = tabItems,
                onSelectedItem = { selectedItem ->
                    selectedTab.value = selectedItem
                    changeScreen(navController, selectedItem)
                }
            )
        }) { paddingValue ->
        NavHost(
            modifier = Modifier
                .padding(paddingValue),
            navController = navController,
            startDestination = tabItems[0].destination
        ) {
            composable<Destination.HomeVendor> {
                VendorHomeScreen()
            }
            composable<Destination.ClientList> {
                ClientListScreen()
            }
            composable<Destination.VendorOrderList> {
                Text(text = "Home Client")
            }
            composable<HomeClientDestination.ProductList> {
                val internalNavController = rememberNavController()
                ProductNavHost(
                    navHostController = internalNavController
                )
            }
            composable<HomeClientDestination.ClientOrderList> {
                OrderListScreen()
            }
            composable<Destination.VisitList> {
                VisitVendorScreen()
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
    onSelectedItem: (BottomTabItem) -> Unit,
    items: List<BottomTabItem>
) {
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
                            style = MaterialTheme.typography.labelMedium
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
