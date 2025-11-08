package com.uniandes.medisupply.presentation.ui.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.uniandes.medisupply.R
import com.uniandes.medisupply.presentation.containers.ComposableActivity
import com.uniandes.medisupply.presentation.ui.feature.product.ProductListScreen
import com.uniandes.medisupply.presentation.ui.theme.MediSupplyTheme

@Composable
fun VendorHomeScreen() {
    val context = LocalContext.current
    VendorHomeContent(
        onProductClicked = {
            val intent = ComposableActivity.createIntent(
                context = context,
                content = {
                    ProductListScreen()
                }
            )
            context.startActivity(intent)
        }
    )
}

@Composable
fun VendorHomeContent(
    onProductClicked: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = onProductClicked
        ) {
            Row {
                Text(stringResource(R.string.view_products))
            }
        }
    }
}

@Composable
@Preview
fun VendorHomeScreenPreview() {
    MediSupplyTheme {
        VendorHomeContent()
    }
}
