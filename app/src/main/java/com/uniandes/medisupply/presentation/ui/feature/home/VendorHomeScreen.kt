package com.uniandes.medisupply.presentation.ui.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.uniandes.medisupply.R
import com.uniandes.medisupply.common.UserDataProvider
import com.uniandes.medisupply.presentation.component.Card
import com.uniandes.medisupply.presentation.containers.ComposableActivity
import com.uniandes.medisupply.presentation.containers.ComposableFlow
import com.uniandes.medisupply.presentation.ui.theme.MediSupplyTheme
import com.uniandes.medisupply.presentation.ui.theme.spaces
import org.koin.compose.getKoin

@Composable
fun VendorHomeScreen(
    userDataProvider: UserDataProvider = getKoin().get()
) {
    val context = LocalContext.current
    VendorHomeContent(
        vendorName = userDataProvider.getName(),
        onProductClicked = {
            val intent = ComposableActivity.createIntent(
                context = context,
                flow = ComposableFlow.ProductFlow
            )
            context.startActivity(intent)
        }
    )
}

@Composable
fun VendorHomeContent(
    onProductClicked: () -> Unit = {},
    vendorName: String
) {
    Box(
        modifier = Modifier.fillMaxSize()
            .padding(MaterialTheme.spaces.medium),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column (
                    modifier = Modifier.padding(MaterialTheme.spaces.medium)
                ) {
                    Text(
                        text = stringResource(R.string.welcome_vendor_message, vendorName)
                    )
                }
            }
        }
        FloatingActionButton(
            onClick = onProductClicked
        ) {
            Row(
                Modifier.padding(MaterialTheme.spaces.small)
            ) {
                Text(stringResource(R.string.view_products))
            }
        }
    }
}

@Composable
@Preview
fun VendorHomeScreenPreview() {
    MediSupplyTheme {
        VendorHomeContent(vendorName = "nombre vendedor")
    }
}
