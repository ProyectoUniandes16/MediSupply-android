package com.uniandes.medisupply.screens

import androidx.activity.ComponentActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.uniandes.medisupply.R
import com.uniandes.medisupply.presentation.model.OrderStatusUI
import com.uniandes.medisupply.presentation.model.OrderUI
import com.uniandes.medisupply.presentation.model.ProductUI
import com.uniandes.medisupply.presentation.ui.feature.order.OrderDetailContent
import com.uniandes.medisupply.presentation.viewmodel.order.OrderDetailUiState

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertTrue

class OrderDetailScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    val uiState = MutableStateFlow(
        OrderDetailUiState(
            order = OrderUI(
                id = 1234,
                status = OrderStatusUI.DELIVERED,
                total = 150.0,
                clientId = 1,
                orderDate = "2025-09-16",
                deliveryDate = "2025-09-30",
                totalProducts = 30,
                products = List(5) {
                    Pair(
                        ProductUI(
                            id = it,
                            name = "Product $it",
                            price = 10.0,
                            category = "Category",
                        ), it * 2
                    )
                }
            )
        )
    )
    private var onBackClickedInvoked = false

    @Before
    fun setUp() {
        // when
        onBackClickedInvoked = false
        composeTestRule.setContent {
            val uiState = uiState.collectAsState()
            OrderDetailContent(
                uiState = uiState.value,
                onBackClick = {
                    onBackClickedInvoked = true
                }
            )
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun orderDetailScreen_displaysOrderDetails() {
        // given
        val newState = uiState.value.copy()

        // when
        uiState.update { newState }
        // then
        composeTestRule.onNodeWithText(text = "#${newState.order.id}").assertIsDisplayed()
    }

    @Test
    fun orderDetailScreen_displaysError_and_Dismiss() {
        // given
        val error = "Error loading order details"
        val newState = uiState.value.copy(
            error = error,
            showError = true,
            isLoading = false
        )

        // when
        uiState.update { newState }
        // then
        composeTestRule.onNodeWithText(text = newState.error!!).assertIsDisplayed()
        composeTestRule.onNodeWithText(text = composeTestRule.activity.getString(R.string.ok)).performClick()
        assertTrue(onBackClickedInvoked)
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
    fun orderDetailScreen_onBackClick_invoked() {
        // given
        val newState = uiState.value.copy()

        // when
        uiState.update { newState }
        // then
        composeTestRule.onNodeWithText(text = "#${newState.order.id}").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(
            composeTestRule.activity.getString(R.string.back)
        ).performClick()
        assert(onBackClickedInvoked)
    }
}
