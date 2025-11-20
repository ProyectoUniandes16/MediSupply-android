package com.uniandes.medisupply.screens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.uniandes.medisupply.R
import com.uniandes.medisupply.presentation.model.VisitUI
import com.uniandes.medisupply.presentation.model.VisitStatusUI
import com.uniandes.medisupply.presentation.ui.feature.home.VisitVendorContent
import com.uniandes.medisupply.presentation.viewmodel.vendor.VisitUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VisitVendorScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun sampleVisits(count: Int): List<VisitUI> = List(count) { i ->
        VisitUI(
            status = when (i % 3) {
                0 -> VisitStatusUI.COMPLETED
                1 -> VisitStatusUI.PENDING
                else -> VisitStatusUI.IN_PROGRESS
            },
            visitDate = "2025-11-21",
            clientName = "Client $i",
            clientAddress = "Address $i",
            contactName = "Contact $i"
        )
    }

    @Test
    fun showsLoadingIndicator_whenIsLoadingTrue() {
        val uiState = VisitUiState(isLoading = true, selectedDate = "2025-11-21")
        composeTestRule.setContent {
            VisitVendorContent(uiState = uiState, onEvent = {})
        }

        composeTestRule.onNodeWithTag("LOADING_INDICATOR").assertIsDisplayed()
    }

    @Test
    fun showsNoVisitsMessage_whenVisitListEmpty() {
        val uiState = VisitUiState(isLoading = false, visitList = emptyList(), selectedDate = "2025-11-21")
        val expected = composeTestRule.activity.getString(R.string.no_visits_today)

        composeTestRule.setContent {
            VisitVendorContent(uiState = uiState, onEvent = {})
        }

        composeTestRule.onNodeWithText(expected).assertIsDisplayed()
    }

    @Test
    fun showsVisitItems_whenVisitListNotEmpty() {
        val visits = sampleVisits(3)
        val uiState = VisitUiState(isLoading = false, visitList = visits, selectedDate = "2025-11-21")

        composeTestRule.setContent {
            VisitVendorContent(uiState = uiState, onEvent = {})
        }

        // Check that each client name is displayed and position numbers exist
        visits.forEachIndexed { index, visit ->
            composeTestRule.onNodeWithText(visit.clientName).assertIsDisplayed()
            // AvatarText shows the position as number (position = index + 1)
            composeTestRule.onNodeWithText((index + 1).toString()).assertIsDisplayed()
        }
    }

    @Test
    fun showsErrorDialog_whenShowErrorTrue() {
        val errorMessage = "Test error message"
        val uiState = VisitUiState(isLoading = false, showError = true, errorMessage = errorMessage, selectedDate = "2025-11-21")

        composeTestRule.setContent {
            VisitVendorContent(uiState = uiState, onEvent = {})
        }

        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
        val retry = composeTestRule.activity.getString(R.string.retry)
        composeTestRule.onNodeWithText(retry).assertIsDisplayed()
    }
}
