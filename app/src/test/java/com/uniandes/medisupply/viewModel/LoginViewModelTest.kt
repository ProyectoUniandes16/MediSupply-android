package com.uniandes.medisupply.viewModel

import com.uniandes.medisupply.common.AppDestination
import com.uniandes.medisupply.common.InternalNavigator
import com.uniandes.medisupply.common.ResourcesProvider
import com.uniandes.medisupply.common.UserDataProvider
import com.uniandes.medisupply.domain.model.User
import com.uniandes.medisupply.domain.model.UserRole
import com.uniandes.medisupply.domain.repository.UserRepository
import com.uniandes.medisupply.presentation.navigation.Destination
import com.uniandes.medisupply.presentation.viewmodel.LoginViewModel
import com.uniandes.medisupply.presentation.viewmodel.LoginViewModel.UserEvent
import io.mockk.coEvery
import io.mockk.coVerify
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
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    private lateinit var loginViewModel: LoginViewModel
    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()
    private val userDataProvider = mockk<UserDataProvider>(relaxed = true)
    private val resourcesProvider = mockk<ResourcesProvider>(relaxed = true)
    private val internalNavigator = mockk<InternalNavigator>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { userDataProvider.isUserLoggedIn() } returns false
        loginViewModel = LoginViewModel(
            testDispatcher,
            internalNavigator,
            userRepository,
            userDataProvider,
            resourcesProvider
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onEvent PrimaryButtonClicked SHOULD login WHEN is not new user and email and password are valid`() = runTest {
        // Given
        val email = "john.archibald.campbell@example-pet-store.com"
        val password = "password"
        val token = "token_123"
        val result = Result.success(Pair(User(1, "John", email, UserRole.VENDOR), token))
        coEvery { userRepository.login(email, password) } returns result

        // When
        loginViewModel.onEvent(UserEvent.OnEmailChange(email))
        loginViewModel.onEvent(UserEvent.OnPasswordChange(password))
        loginViewModel.onEvent(UserEvent.OnPrimaryButtonClick)

        // Then
        assertEquals(email, loginViewModel.uiState.value.email)
        assertEquals(password, loginViewModel.uiState.value.password)

        coVerify { userDataProvider.setUserData(token, result.getOrNull()!!.first) }

        val slot = slot<AppDestination>()
        coVerify { internalNavigator.requestDestination(capture(slot)) }
        assertTrue(slot.captured is AppDestination.HomeClient)
        val userResult = (slot.captured as AppDestination.HomeClient).extras["user"]
        assertTrue(userResult is User)
        assertEquals(result.getOrNull()!!.first, userResult)
    }

    @Test
    fun `onEvent PrimaryButtonClicked SHOULD show error when is not new user and login failed`() = runTest { // Given
        val email = "john.archibald.campbell@example-pet-store.com"
        val password = "password"
        val token = "token_123"
        val result: Result<Pair<User, String>> = Result.failure(Exception("Invalid credentials"))
        coEvery { userRepository.login(email, password) } returns result

        // When
        loginViewModel.onEvent(UserEvent.OnEmailChange(email))
        loginViewModel.onEvent(UserEvent.OnPasswordChange(password))
        loginViewModel.onEvent(UserEvent.OnPrimaryButtonClick)

        // Then
        assertEquals(email, loginViewModel.uiState.value.email)
        assertEquals(password, loginViewModel.uiState.value.password)

        coVerify(atLeast = 0) { userDataProvider.setUserData(any(), any()) }
        assertTrue(loginViewModel.uiState.value.showError)
        assertEquals("Invalid credentials", loginViewModel.uiState.value.error)
    }

    @Test
    fun `onEvent SecondaryButtonClicked SHOULD update isLogin to false when isLogin is true`() = runTest {
        // When
        loginViewModel.onEvent(UserEvent.OnSecondaryButtonClick)

        // Then
        assertFalse(loginViewModel.uiState.value.isLogin)
    }

    @Test
    fun `onEvent SecondaryButtonClicked SHOULD update isLogin to true when isLogin is false`() = runTest {
        // Given
        loginViewModel.onEvent(UserEvent.OnSecondaryButtonClick) // Switch to register
        // When
        loginViewModel.onEvent(UserEvent.OnSecondaryButtonClick)

        // Then
        assertTrue(loginViewModel.uiState.value.isLogin)
    }

    @Test
    fun `onEvent PrimaryButtonClicked SHOULD navigate to register when isLogin is false`() = runTest {
        // Given
        val email = "john.archibald.campbell@example-pet-store.com"
        val password = "password"
        loginViewModel.onEvent(UserEvent.OnSecondaryButtonClick) // Switch to register
        // When
        loginViewModel.onEvent(UserEvent.OnEmailChange(email))
        loginViewModel.onEvent(UserEvent.OnPasswordChange(password))
        loginViewModel.onEvent(UserEvent.OnPrimaryButtonClick)

        // Then
        assertEquals(email, loginViewModel.uiState.value.email)
        assertEquals(password, loginViewModel.uiState.value.password)
        verify {
            internalNavigator.navigateTo(
                Destination.NewClient,
                mapOf(
                    Destination.NewClient.PRE_FILLED_EMAIL to email,
                    Destination.NewClient.PRE_FILLED_PASSWORD to password,
                    Destination.NewClient.IS_NEW_USER to true
                )
            )
        }
    }
}
