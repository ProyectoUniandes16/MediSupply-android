package com.uniandes.medisupply.screens

import androidx.activity.ComponentActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.uniandes.medisupply.presentation.model.OrderStatusUI
import com.uniandes.medisupply.presentation.model.OrderUI
import com.uniandes.medisupply.presentation.ui.feature.home.ClientOrderListContent
import com.uniandes.medisupply.presentation.viewmodel.client.OrderListUiState

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test

class OrderListScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    val uiState = MutableStateFlow(
        OrderListUiState()
    )

    @Before
    fun setUp() {
        // when
        composeTestRule.setContent {
            val uiState = uiState.collectAsState()
            ClientOrderListContent(
                uiState = uiState.value,
                onEvent = {
                }
            )
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun orderDetailScreen_displaysError() {
        // given
        val error = "Error loading order list"
        val newState = uiState.value.copy(
            error = error,
            hasError = true,
            isLoading = false
        )

        // when
        uiState.update { newState }
        // then
        composeTestRule.onNodeWithText(text = newState.error!!).assertIsDisplayed()
    }

    @Test
    fun orderDetailScreen_displayLoading() {
        // given
        val newState = uiState.value.copy(
            isLoading = true
        )

        // when
        uiState.update { newState }
        // then
        composeTestRule
            .onNodeWithTag("LOADING")
            .assertIsDisplayed()
    }

    @Test
    fun orderListScreen_displaysOrderList() {
        // given
        val newState = uiState.value.copy(
            isLoading = false,
            hasError = false,
            error = null,
            orders = List(5) {
                OrderUI(
                    id = it,
                    status = OrderStatusUI.PENDING,
                    total = 100.0,
                    orderDate = "2024-01-01",
                    deliveryDate = "2024-01-02",
                    totalProducts = 100,
                    clientId = 1
                )
            }
        )

        // when
        uiState.update { newState }
        // then
        composeTestRule
            .onNodeWithText(text = "#${newState.displayedOrders.first().id}")
            .assertIsDisplayed()
    }
}
