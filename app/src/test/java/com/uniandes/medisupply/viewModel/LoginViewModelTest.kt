package com.uniandes.medisupply.viewModel

import com.uniandes.medisupply.common.AppDestination
import com.uniandes.medisupply.common.NavigationProvider
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

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        loginViewModel = LoginViewModel(
            testDispatcher,
            navigationProvider,
            userRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onEvent PrimaryButtonClicked SHOULD call login WHEN email and password are valid`() = runTest {
        // Given
        val email = "john.archibald.campbell@example-pet-store.com"
        val password = "password"
        val result = Result.success(User(10, "name", email))
        coEvery { userRepository.login(email, password) } returns result

        // When
        loginViewModel.onEvent(UserEvent.OnEmailChange(email))
        loginViewModel.onEvent(UserEvent.OnPasswordChange(password))
        loginViewModel.onEvent(UserEvent.OnPrimaryButtonClick)

        // Then
        assertEquals(email, loginViewModel.uiState.value.email)
        assertEquals(password, loginViewModel.uiState.value.password)
        val slot = slot<AppDestination>()
        coVerify { navigationProvider.requestDestination(capture(slot)) }
        assertTrue(slot.captured is AppDestination.HomeClient)
        val userResult = (slot.captured as AppDestination.HomeClient).extras["user"]
        assertTrue(userResult is User)
        assertEquals(result.getOrNull(), userResult)
    }
}