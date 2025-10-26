package com.uniandes.medisupply.viewModel

import com.uniandes.medisupply.common.AppDestination
import com.uniandes.medisupply.common.NavigationProvider
import com.uniandes.medisupply.common.UserDataProvider
import com.uniandes.medisupply.domain.model.User
import com.uniandes.medisupply.domain.repository.ClientRepository
import com.uniandes.medisupply.domain.repository.UserRepository
import com.uniandes.medisupply.presentation.viewmodel.LoginViewModel.UserEvent
import com.uniandes.medisupply.presentation.viewmodel.NewClientViewModel
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

@ExperimentalCoroutinesApi
class NewClientLoginViewModelTest {

    private lateinit var newClientLoginViewModel: NewClientViewModel
    private val clientRepository = mockk<ClientRepository>(relaxed = true)
    private val navigationProvider: NavigationProvider = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        newClientLoginViewModel = NewClientViewModel(
            clientRepository,
            navigationProvider
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onEvent OnNameChange SHOULD update name`() = runTest {
    }

    @Test
    fun `onEvent PrimaryButtonClicked SHOULD show error when login failed`() = runTest {
    }
}