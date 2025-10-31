package com.uniandes.medisupply.viewModel

import com.uniandes.medisupply.common.NavigationProvider
import com.uniandes.medisupply.common.ResourcesProvider
import com.uniandes.medisupply.domain.repository.ClientRepository
import com.uniandes.medisupply.presentation.viewmodel.NewClientViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExperimentalCoroutinesApi
class NewClientViewModelTest {

    private lateinit var viewModel: NewClientViewModel
    private val clientRepository = mockk<ClientRepository>(relaxed = true)
    private val navigationProvider: NavigationProvider = mockk(relaxed = true)
    private val resourcesProvider = mockk<ResourcesProvider>(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()

    companion object {
        const val name = "name"
        val type = "Hospital"
        const val nit = "0000000001"
        const val address = "address"
        const val country = "country"
        const val position = "position"
        const val contactName = "contactName"
        const val contactPhone = "1234567890"
        const val contactEmail = "email@email.com"
        const val companyEmail = "companyEmail@email.com"
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery {
            resourcesProvider.getString(any())
        } returns "Hospital"
        viewModel = NewClientViewModel(
            clientRepository,
            navigationProvider,
            resourcesProvider
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fill all client fields successfully WHEN all fields are valid`() = runTest {
        // Given
        coEvery { clientRepository.addClient(
            name,
            type,
            nit,
            address,
            country,
            contactName,
            position,
            contactPhone,
            contactEmail,
            companyEmail
        ) } returns Result.success(true)

        // When
        viewModel.onEvent(NewClientViewModel.UserEvent.OnNameChange(name))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnTypeChange(type))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnNitChange(nit))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnAddressChange(address))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnCountryChange(country))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnPositionChange(position))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnContactNameChange(contactName))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnContactPhoneChange(contactPhone))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnContactEmailChange(contactEmail))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnCompanyEmailChange(companyEmail))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnSaveClientClick)

        // Then
        assertEquals(name, viewModel.uiState.value.name)
        assertEquals(type, viewModel.uiState.value.type)
        assertEquals(nit, viewModel.uiState.value.nit)
        assertEquals(address, viewModel.uiState.value.address)
        assertEquals(country, viewModel.uiState.value.country)
        assertEquals(position, viewModel.uiState.value.position)
        assertEquals(contactName, viewModel.uiState.value.contactName)
        assertEquals(contactPhone, viewModel.uiState.value.contactPhone)
        assertEquals(contactEmail, viewModel.uiState.value.contactEmail)
        assertEquals(companyEmail, viewModel.uiState.value.companyEmail)
        assertEquals(true, viewModel.uiState.value.primaryButtonEnabled)
        assertEquals(false, viewModel.uiState.value.showError)
        assertEquals(false, viewModel.uiState.value.isLoading)
        coVerify {
            clientRepository.addClient(
                name,
                type,
                nit,
                address,
                country,
                contactName,
                position,
                contactPhone,
                contactEmail,
                companyEmail
            )
        }
        coVerify {
            navigationProvider.finishCurrentDestination(
                success = true
            )
        }
    }

    @Test
    fun `onEvent OnSaveClientClick SHOULD show error when failed`() = runTest {
        // Given
        val errorMessage = "Network Error"
        coEvery {
            clientRepository.addClient(
                name,
                type,
                nit,
                address,
                country,
                contactName,
                position,
                contactPhone,
                contactEmail,
                companyEmail
            )
        } returns Result.failure(Exception(errorMessage))

        // When
        viewModel.onEvent(NewClientViewModel.UserEvent.OnNameChange(name))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnTypeChange(type))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnNitChange(nit))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnAddressChange(address))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnCountryChange(country))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnPositionChange(position))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnContactNameChange(contactName))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnContactPhoneChange(contactPhone))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnContactEmailChange(contactEmail))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnCompanyEmailChange(companyEmail))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnSaveClientClick)

        // Then
        coVerify {
            clientRepository.addClient(
                name,
                type,
                nit,
                address,
                country,
                contactName,
                position,
                contactPhone,
                contactEmail,
                companyEmail
            )
        }
        assertEquals(true, viewModel.uiState.value.showError)
        assertEquals(errorMessage, viewModel.uiState.value.error)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `onEvent OnDismissErrorDialog SHOULD hide error dialog`() = runTest {
        // Given
        val errorMessage = "Network Error"
        coEvery {
            clientRepository.addClient(
                name,
                type,
                nit,
                address,
                country,
                contactName,
                position,
                contactPhone,
                contactEmail,
                companyEmail
            )
        } returns Result.failure(Exception(errorMessage))

        // When
        viewModel.onEvent(NewClientViewModel.UserEvent.OnNameChange(name))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnTypeChange(type))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnNitChange(nit))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnAddressChange(address))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnCountryChange(country))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnPositionChange(position))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnContactNameChange(contactName))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnContactPhoneChange(contactPhone))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnContactEmailChange(contactEmail))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnCompanyEmailChange(companyEmail))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnSaveClientClick)

        // Then
        viewModel.onEvent(NewClientViewModel.UserEvent.OnDismissErrorDialog)

        // Then
        assertEquals(false, viewModel.uiState.value.showError)
    }

    @Test
    fun `onEvent OnBackClick SHOULD navigate back`() = runTest {
        // When
        viewModel.onEvent(NewClientViewModel.UserEvent.OnBackClick)

        // Then
        coVerify {
            navigationProvider.finishCurrentDestination()
        }
    }

    @Test
    fun `button SHOULD be disabled WHEN fields are empty`() = runTest {
        // Then
        assertEquals(false, viewModel.uiState.value.primaryButtonEnabled)
    }

    @Test
    fun `onEvent OnCompanyEmailChange SHOULD show error WHEN email is invalid`() = runTest {
        // When
        viewModel.onEvent(NewClientViewModel.UserEvent.OnCompanyEmailChange("invalidEmail"))

        // Then
        assertEquals(false, viewModel.uiState.value.primaryButtonEnabled)
        assertNotNull(viewModel.uiState.value.errorCompanyEmail)
    }

    @Test
    fun `onEvent OnContactEmailChange SHOULD show error WHEN email is invalid`() = runTest {
        // When
        viewModel.onEvent(NewClientViewModel.UserEvent.OnContactEmailChange("invalidEmail"))

        // Then
        assertEquals(false, viewModel.uiState.value.primaryButtonEnabled)
        assertNotNull(viewModel.uiState.value.errorContactEmail)
    }

    @Test
    fun `onEvent OnContactPhoneChange SHOULD show error WHEN email is invalid`() = runTest {
        // When
        viewModel.onEvent(NewClientViewModel.UserEvent.OnContactPhoneChange("invalidEmail"))

        // Then
        assertEquals(false, viewModel.uiState.value.primaryButtonEnabled)
        assertNotNull(viewModel.uiState.value.errorContactPhone)
    }
}
