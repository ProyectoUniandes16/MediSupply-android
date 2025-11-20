package com.uniandes.medisupply.screens

import androidx.activity.ComponentActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.uniandes.medisupply.R
import com.uniandes.medisupply.domain.model.Client
import com.uniandes.medisupply.domain.model.ClientContactInfo
import com.uniandes.medisupply.presentation.ui.feature.order.ClientOrderContent
import com.uniandes.medisupply.presentation.viewmodel.order.CreateOrderUiState
import com.uniandes.medisupply.screens.utils.PRODUCT_UI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ClientOrderScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val uiState = MutableStateFlow(
        CreateOrderUiState()
    )

    @Before
    fun setUp() {
        composeTestRule.setContent {
            val uiState = uiState.collectAsState()
            ClientOrderContent(
                client = CLIENT,
                uiState = uiState.value,
                onEvent = {}
            )
        }

        composeTestRule.waitForIdle()
        // Setup code for the tests will go here
    }

    @Test
    fun clientOrderScreen_displayedCorrectly_with_no_products() {
        // given
        val client = CLIENT
        val newState = uiState.value.copy()
        // when
        uiState.update { newState }
        // then
        composeTestRule.onNodeWithText(client.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(R.string.empty_order)
        ).assertIsDisplayed()
    }

    @Test
    fun clientOrderScreen_displayedCorrectly_with_products() {
        // given
        val client = CLIENT
        val newState = buildProductSuccessOrderState()
        // when
        uiState.update { newState }
        // then
        composeTestRule.onNodeWithText(client.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(R.string.empty_order)
        ).assertIsNotDisplayed()
        composeTestRule.onNodeWithText(
            uiState.value.productOrder.first().first.name
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(
           uiState.value.productOrder.first().second.toString()
        ).assertIsDisplayed()
    }

    @Test
    fun clientOrderScreen_displayedCorrectly_confirmation() {
        // given
        val client = CLIENT
        val newState = buildProductSuccessOrderState().copy(isConfirmation = true)
        // when
        uiState.update { newState }
        // then
        composeTestRule.onNodeWithText(client.name).assertIsDisplayed()

        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(R.string.empty_order)
        ).assertIsNotDisplayed()

        composeTestRule.onNodeWithText(
            uiState.value.productOrder.first().first.name
        ).assertIsDisplayed()

        composeTestRule.onNodeWithText(
            uiState.value.productOrder.first().second.toString()
        ).assertIsDisplayed()

        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(R.string.confirm_order)
        ).assertIsDisplayed()

        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(R.string.add_product)
        ).assertIsNotDisplayed()

        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(R.string.edit_order)
        ).assertIsDisplayed()
    }

    @Test
    fun clientOrderScreen_displayedCorrectly_is_confirmation_loading() {
        // given
        val client = CLIENT
        val newState = buildProductSuccessOrderState().copy(isConfirmation = true, isLoadingConfirmation = true)
        // when
        uiState.update { newState }
        // then
        composeTestRule.onNodeWithTag(
            "CONFIRMATION_ORDER_LOADING_INDICATOR"
        ).assertIsDisplayed()
    }

    @Test
    fun clientOrderScreen_showProductsList_whenProductsExist() {
        // given
        val newState = CreateOrderUiState(
            productList = List(10) {
                PRODUCT_UI.copy(
                    id = it,
                    name = "Product $it",
                    availableStock = it
                )
            },
            showProductBottomSheet = true,
        )

        // when
        uiState.value = newState

        composeTestRule.waitUntil(
            condition = {
                composeTestRule
                    .onAllNodesWithText(newState.productList.first().name)
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            },
            timeoutMillis = 10_000L
        )
        // then
        composeTestRule.onNodeWithText(
            newState.productList.first().name,
        ).assertIsDisplayed()

        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(
                R.string.available_stock,
                newState.productList.first().availableStock
            )
        ).assertIsDisplayed()
    }

    @Test
    fun clientOrderScreen_showNoProductsMessage_whenNoProductsExist() {
        // given
        val newState = CreateOrderUiState(
            productList = emptyList(),
            showProductBottomSheet = true,
        )

        // when
        uiState.value = newState
        composeTestRule.waitForIdle()
        // then
        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(R.string.no_products_found)
        ).assertIsDisplayed()
    }

    companion object {
        val CLIENT = Client(
            id = 1,
            name = "HealthCorp",
            contactInfo = ClientContactInfo(
                name = "Alice Smith",
                email = "",
                phone = "555-1234",
                position = "Procurement Manager"
            ),
            address = "123 Main St",
            email = ""
        )
    }

    fun buildProductSuccessOrderState() = uiState.value.copy(
        productOrder = List(1) {
            Pair(
                PRODUCT_UI.copy(
                    id = it,
                    name = "Product $it",
                    availableStock = it
                ),
                it + 1
            )
        },
        totalAmount = 150.0,
    )
}
