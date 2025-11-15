package com.uniandes.medisupply.viewModel

import com.uniandes.medisupply.common.AppDestination
import com.uniandes.medisupply.common.InternalNavigator
import com.uniandes.medisupply.common.ResourcesProvider
import com.uniandes.medisupply.common.UserDataProvider
import com.uniandes.medisupply.domain.model.User
import com.uniandes.medisupply.domain.model.UserRole
import com.uniandes.medisupply.domain.repository.ClientRepository
import com.uniandes.medisupply.domain.repository.UserRepository
import com.uniandes.medisupply.presentation.navigation.Destination
import com.uniandes.medisupply.presentation.viewmodel.NewClientViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
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
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class NewClientViewModelTest {

    private lateinit var viewModel: NewClientViewModel
    private val clientRepository = mockk<ClientRepository>(relaxed = true)
    private val resourcesProvider = mockk<ResourcesProvider>(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()
    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val userDataProvider = mockk<UserDataProvider>(relaxed = true)
    private val internalNavigator = mockk<InternalNavigator>(relaxed = true)

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
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = NewClientViewModel(
            clientRepository,
            internalNavigator,
            resourcesProvider,
            userRepository,
            userDataProvider,
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
            internalNavigator.finishCurrentDestination(
                success = true
            )
        }
    }

    @Test
    fun `onEvent OnSaveClientClick SHOULD show error when add client failed`() = runTest {
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
            internalNavigator.stepBack()
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

    @Test
    fun `onEvent OnNitChange SHOULD show error WHEN NIT is invalid`() = runTest {
        // When
        viewModel.onEvent(NewClientViewModel.UserEvent.OnNitChange("invalidNIT"))

        // Then
        assertEquals(false, viewModel.uiState.value.primaryButtonEnabled)
        assertNotNull(viewModel.uiState.value.errorNit)
    }

    @Test
    fun `init SHOULD hide company email field and set up company email and password WHEN is new user`() = runTest {
        // Given
        val password = "DEFAULTPASSWORD"
        every { internalNavigator.getParam(Destination.NewClient.IS_NEW_USER) } returns true
        every { internalNavigator.getParam(Destination.NewClient.PRE_FILLED_EMAIL) } returns companyEmail
        every { internalNavigator.getParam(Destination.NewClient.PRE_FILLED_PASSWORD) } returns password

        // When
        initViewModel()

        // Then
        assertFalse(viewModel.uiState.value.showCompanyEmailField)
        assertEquals(companyEmail, viewModel.uiState.value.companyEmail)
        assertEquals(password, internalNavigator.getParam(Destination.NewClient.PRE_FILLED_PASSWORD))
    }

    @Test
    fun `onEvent OnSaveClientClick SHOULD show error when signUpClient failed`() = runTest {
        // Given
        val password = "DEFAULTPASSWORD"
        every { internalNavigator.getParam(Destination.NewClient.IS_NEW_USER) } returns true
        every { internalNavigator.getParam(Destination.NewClient.PRE_FILLED_EMAIL) } returns companyEmail
        every { internalNavigator.getParam(Destination.NewClient.PRE_FILLED_PASSWORD) } returns password

        val errorMessage = "Network Error"
        coEvery {
            userRepository.signUpClient(
                name,
                companyEmail,
                password,
                contactName,
                contactEmail,
                contactPhone,
                address,
                nit,
                country,
                type,
                position
            )
        } returns Result.failure(Exception(errorMessage))

        // When
        initViewModel()
        viewModel.onEvent(NewClientViewModel.UserEvent.OnNameChange(name))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnTypeChange(type))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnNitChange(nit))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnAddressChange(address))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnCountryChange(country))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnPositionChange(position))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnContactNameChange(contactName))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnContactPhoneChange(contactPhone))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnContactEmailChange(contactEmail))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnSaveClientClick)

        // Then
        coVerify {
            userRepository.signUpClient(
                name,
                companyEmail,
                password,
                contactName,
                contactEmail,
                contactPhone,
                address,
                nit,
                country,
                type,
                position
            )
        }
        assertEquals(true, viewModel.uiState.value.showError)
        assertEquals(errorMessage, viewModel.uiState.value.error)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `onEvent OnSaveClientClick SHOULD request Home destination when signUpClient is successful`() = runTest {
        // Given
        val password = "DEFAULTPASSWORD"
        every { internalNavigator.getParam(Destination.NewClient.IS_NEW_USER) } returns true
        every { internalNavigator.getParam(Destination.NewClient.PRE_FILLED_EMAIL) } returns companyEmail
        every { internalNavigator.getParam(Destination.NewClient.PRE_FILLED_PASSWORD) } returns password
        val user = User(1, name, companyEmail, UserRole.VENDOR)
        val token = "token_123"
        val result = Result.success(Pair(user, token))
        coEvery {
            userRepository.signUpClient(
                name,
                companyEmail,
                password,
                contactName,
                contactEmail,
                contactPhone,
                address,
                nit,
                country,
                type,
                position
            )
        } returns result

        // When
        initViewModel()
        viewModel.onEvent(NewClientViewModel.UserEvent.OnNameChange(name))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnTypeChange(type))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnNitChange(nit))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnAddressChange(address))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnCountryChange(country))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnPositionChange(position))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnContactNameChange(contactName))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnContactPhoneChange(contactPhone))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnContactEmailChange(contactEmail))
        viewModel.onEvent(NewClientViewModel.UserEvent.OnSaveClientClick)

        // Then
        coVerify {
            userRepository.signUpClient(
                name,
                companyEmail,
                password,
                contactName,
                contactEmail,
                contactPhone,
                address,
                nit,
                country,
                type,
                position
            )
        }
        val slot = slot<AppDestination>()
        verify { internalNavigator.requestDestination(capture(slot)) }
        assertTrue(slot.captured is AppDestination.HomeClient)
        val userResult = (slot.captured as AppDestination.HomeClient).extras["user"]
        assertTrue(userResult is User)
        assertEquals(user, userResult)
        assertEquals(false, viewModel.uiState.value.showError)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }
}
