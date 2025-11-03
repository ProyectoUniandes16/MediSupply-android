
package com.uniandes.medisupply.presentation.ui.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.uniandes.medisupply.R
import com.uniandes.medisupply.presentation.ui.theme.MediSupplyTheme
import com.uniandes.medisupply.presentation.ui.theme.spaces
import androidx.compose.material3.MaterialTheme
import com.uniandes.medisupply.presentation.viewmodel.order.OrderListViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OrderListScreen(
    modifier: Modifier = Modifier,
    viewModel: OrderListViewModel = koinViewModel()
) {
    OrderListContent(
        modifier = modifier,
        onNewOrderClicked = {
            viewModel.onEvent(OrderListViewModel.UserEvent.OnNewOrderClicked)
        }
    )
}

@Composable
internal fun OrderListContent(
    modifier: Modifier = Modifier,
    onNewOrderClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            modifier = Modifier
                .padding(MaterialTheme.spaces.medium),
            onClick = onNewOrderClicked
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = MaterialTheme.spaces.medium,
                    vertical = MaterialTheme.spaces.small
                )
            ) {
                Text(stringResource(R.string.new_order))
            }
        }
    }
}

@Composable
@Preview
fun ClDetailContentPreview() {
    MediSupplyTheme {
        OrderListContent(
            onNewOrderClicked = {}
        )
    }
}
