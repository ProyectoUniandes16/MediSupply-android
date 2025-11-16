package com.uniandes.medisupply.screens

import androidx.activity.ComponentActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.uniandes.medisupply.R
import com.uniandes.medisupply.domain.model.ClientType
import com.uniandes.medisupply.presentation.ui.feature.client.NewClientContent
import com.uniandes.medisupply.presentation.viewmodel.NewClientUiState
import com.uniandes.medisupply.presentation.viewmodel.NewClientViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertTrue

class NewClientScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    private val uiState = MutableStateFlow(
        NewClientUiState()
    )

    private val eventState: MutableStateFlow<NewClientViewModel.UserEvent?> = MutableStateFlow(
        null
    )

    @Before
    fun setUp() {
        // when
        composeTestRule.setContent {
            val uiState = uiState.collectAsState()
            NewClientContent(
                uiState = uiState.value,
                onUserEvent = {
                    eventState.value = it
                }
            )
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun newClientScreen_displayedCorrectly() {
        // then
        val activity = composeTestRule.activity

        assertTextDisplayed(activity.getString(R.string.client_data))
        assertTextDisplayed(activity.getString(R.string.company_name))
        assertTextDisplayed(activity.getString(R.string.type))

        composeTestRule.onNodeWithText(
            activity.getString(R.string.nit)
        ).assertIsDisplayed()

        composeTestRule.onNodeWithText(
            activity.getString(R.string.country)
        ).assertIsDisplayed()

        composeTestRule.onNodeWithText(
            activity.getString(R.string.address)
        ).assertIsDisplayed()

        composeTestRule.onNodeWithText(
            activity.getString(R.string.email_company)
        ).assertIsDisplayed()

        composeTestRule.onNodeWithText(
            activity.getString(R.string.contact_name)
        ).assertIsDisplayed()

        composeTestRule.onNodeWithText(
            activity.getString(R.string.position)
        ).performScrollTo().assertIsDisplayed()

        composeTestRule.onNodeWithText(
            activity.getString(R.string.email_contact)
        ).performScrollTo().assertIsDisplayed()

        composeTestRule.onNodeWithText(
            activity.getString(R.string.phone_number)
        ).performScrollTo().assertIsDisplayed()

        composeTestRule.onNodeWithText(
            activity.getString(R.string.register_client)
        ).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun newClientScreen_filled_values() {
        // given
        val activity = composeTestRule.activity
        val newState = buildValidState(activity)

        // when
        uiState.update { newState }
        // then
        assertTextDisplayed(uiState.value.name)
        assertTextDisplayed(uiState.value.nit)
        assertTextDisplayed(uiState.value.address)
        assertTextDisplayed(uiState.value.companyEmail)
        assertTextDisplayed(uiState.value.contactName)
        assertTextDisplayed(uiState.value.position)
        assertTextDisplayed(uiState.value.contactEmail)
        assertTextDisplayed(uiState.value.contactPhone)
        composeTestRule.onNodeWithText(
            activity.getString(R.string.register_client)
        ).performScrollTo().assertIsEnabled()
            .performClick()
    }

    @Test
    fun newClientScreen_show_error_on_fields() {
        // given
        val newState = uiState.value.copy(
            errorName = "Error in name field",
            errorNit = "Error in NIT field",
            errorAddress = "Error in address field",
            errorCompanyEmail = "Error in company email field",
            errorContactName = "Error in contact name field",
            errorPosition = "Error in position field",
            errorContactEmail = "Error in contact email field",
            errorContactPhone = "Error in contact phone field",
            errorCountry = "Error in country field",
        )
        // when
        uiState.update { newState }
        // then
        assertTextDisplayed(uiState.value.errorName!!)
        assertTextDisplayed(uiState.value.errorNit!!)
        assertTextDisplayed(uiState.value.errorAddress!!)
        assertTextDisplayed(uiState.value.errorCompanyEmail!!)
        assertTextDisplayed(uiState.value.errorContactName!!)
        assertTextDisplayed(uiState.value.errorPosition!!)
        assertTextDisplayed(uiState.value.errorContactEmail!!)
        assertTextDisplayed(uiState.value.errorContactPhone!!)
    }

    @Test
    fun newClientScreen_show_error_and_dismiss() {
        // given
        val activity = composeTestRule.activity
        val error = "Error on screen"
        val newState = NewClientUiState(
            error = error,
            showError = true,
            isLoading = false
        )

        // when
        uiState.update { newState }
        // then
        composeTestRule.onNodeWithText(
            uiState.value.error!!
        ).assertIsDisplayed()

        // when dismissing error
        composeTestRule.onNodeWithText(activity.getString(R.string.ok))
            .performClick()
        // then
        assertTrue(
            eventState.value is NewClientViewModel.UserEvent.OnDismissErrorDialog
        )
    }

    @Test
    fun newClientScreen_show_loading_indicator() {
        // given
        val newState = NewClientUiState(
            isLoading = true
        )

        // when
        uiState.update { newState }
        // then
        composeTestRule.onNodeWithTag("LOADING").assertIsDisplayed()
    }

    private fun assertTextDisplayed(text: String) {
        composeTestRule.onNodeWithText(text).performScrollTo().assertIsDisplayed()
    }

    private companion object {
        fun buildValidState(activity: ComponentActivity) = NewClientUiState(
            name = "Company XYZ",
            type = activity.getString(R.string.ips),
            nit = "123456789",
            country = "Colombia",
            address = "123 Main",
            companyEmail = "email@mail.com",
            contactName = "John Doe",
            position = "Manager",
            contactEmail = "contact@email.com",
            contactPhone = "555-1234",
            clientTypes = ClientType.entries.associateWith { type ->
                when (type) {
                    ClientType.IPS -> activity.getString(R.string.ips)
                    ClientType.OTHER -> activity.getString(R.string.other)
                    ClientType.CLINIC -> activity.getString(R.string.clinic)
                    ClientType.DISTRIBUTOR -> activity.getString(R.string.distributor)
                    ClientType.EPS_EAPB -> activity.getString(R.string.eps_eapb)
                    ClientType.HOSPITAL -> activity.getString(R.string.hospital)
                    ClientType.LABORATORY -> activity.getString(R.string.laboratory)
                }
            },
            primaryButtonEnabled = true
        )
    }
}
