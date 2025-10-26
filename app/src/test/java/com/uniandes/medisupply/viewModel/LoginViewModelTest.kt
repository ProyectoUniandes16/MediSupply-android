package com.uniandes.medisupply.viewModel

import com.uniandes.medisupply.common.AppDestination
import com.uniandes.medisupply.common.NavigationProvider
import com.uniandes.medisupply.common.UserDataProvider
import com.uniandes.medisupply.domain.model.User
import com.uniandes.medisupply.domain.repository.UserRepository
import com.uniandes.medisupply.presentation.viewmodel.LoginViewModel
import com.uniandes.medisupply.presentation.viewmodel.LoginViewModel.UserEvent
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
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
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    private lateinit var loginViewModel: LoginViewModel
    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val navigationProvider: NavigationProvider = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()
    private val userDataProvider = mockk<UserDataProvider>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { userDataProvider.isUserLoggedIn() } returns false
        loginViewModel = LoginViewModel(
            testDispatcher,
            navigationProvider,
            userRepository,
            userDataProvider
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onEvent PrimaryButtonClicked SHOULD login WHEN email and password are valid`() = runTest {
        // Given
        val email = "john.archibald.campbell@example-pet-store.com"
        val password = "password"
        val token = "token_123"
        val result = Result.success(Pair(User(1, "John", email), token))
        coEvery { userRepository.login(email, password) } returns result

        // When
        loginViewModel.onEvent(UserEvent.OnEmailChange(email))
        loginViewModel.onEvent(UserEvent.OnPasswordChange(password))
        loginViewModel.onEvent(UserEvent.OnPrimaryButtonClick)

        // Then
        assertEquals(email, loginViewModel.uiState.value.email)
        assertEquals(password, loginViewModel.uiState.value.password)

        coVerify { userDataProvider.setAccessToken(token) }
        coVerify { userDataProvider.setUserLoggedIn(true) }

        val slot = slot<AppDestination>()
        coVerify { navigationProvider.requestDestination(capture(slot)) }
        assertTrue(slot.captured is AppDestination.HomeClient)
        val userResult = (slot.captured as AppDestination.HomeClient).extras["user"]
        assertTrue(userResult is User)
        assertEquals(result.getOrNull()!!.first, userResult)
    }

    @Test
    fun `onEvent PrimaryButtonClicked SHOULD show error when login failed`() = runTest {// Given
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

        coVerify(atLeast = 0) { userDataProvider.setAccessToken(token) }
        coVerify(atLeast = 0) { userDataProvider.setUserLoggedIn(true) }
        assertTrue(loginViewModel.uiState.value.showError)
        assertEquals("Invalid credentials", loginViewModel.uiState.value.error)
    }
}