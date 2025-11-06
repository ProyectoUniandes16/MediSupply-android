package com.uniandes.medisupply.viewModel

import com.uniandes.medisupply.common.AppDestination
import com.uniandes.medisupply.common.NavigationProvider
import com.uniandes.medisupply.companion.CLIENT_LIST
import com.uniandes.medisupply.domain.repository.ClientRepository
import com.uniandes.medisupply.presentation.viewmodel.ClientListViewModel
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class ClientListViewModelTest {

    private lateinit var clientListViewModel: ClientListViewModel
    private val navigationProvider: NavigationProvider = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()
    private val clientRepository: ClientRepository = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val clientList = CLIENT_LIST
        coEvery { clientRepository.getClients() } returns Result.success(clientList)
        clientListViewModel = ClientListViewModel(
            navigationProvider = navigationProvider,
            clientRepository = clientRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onEvent  SHOULD update name`() = runTest {
        // when
        clientListViewModel.onEvent(ClientListViewModel.UserEvent.OnNewClientClick)

        // then
        val slot = slot<AppDestination>()
        val slotRequest = slot<Int>()
        // capture both arguments: the destination and the optional request code
        coVerify { navigationProvider.requestDestination(capture(slot), capture(slotRequest)) }
        assertTrue(slot.captured is AppDestination.NewClient)
        // request code should be null for this navigation call
        assertEquals(AppDestination.NewClient.REQUEST_CODE, slotRequest.captured)
    }

    @Test
    fun `loadClients SHOULD load clients correctly`() = runTest {
        // GIVEN
        val clientList = CLIENT_LIST
        coEvery { clientRepository.getClients() } returns Result.success(clientList)
        // WHEN
        clientListViewModel.onEvent(ClientListViewModel.UserEvent.OnRefreshClients)
        // then
        coVerify { clientRepository.getClients() }
        assertEquals(CLIENT_LIST.size, clientListViewModel.uiState.value.clients.size)
    }

    @Test
    fun `init SHOULD show error on failure`() = runTest {
        // GIVEN
        val expectedError = "Error loading clients"
        coEvery { clientRepository.getClients() } returns Result.failure(Exception(expectedError))
        // WHEN
        clientListViewModel.onEvent(ClientListViewModel.UserEvent.OnRefreshClients)
        // then
        assertTrue(clientListViewModel.uiState.value.clients.isEmpty())
        assertTrue(clientListViewModel.uiState.value.showError)
        assertFalse(clientListViewModel.uiState.value.isLoading)
    }

    @Test
    fun `onEvent OnRefreshClients SHOULD load clients correctly`() = runTest {
        // GIVEN
        val clientList = CLIENT_LIST
        coEvery { clientRepository.getClients() } returns Result.success(clientList)
        // WHEN
        clientListViewModel.onEvent(ClientListViewModel.UserEvent.OnRefreshClients)
        // then
        coVerify { clientRepository.getClients() }
        assertEquals(CLIENT_LIST.size, clientListViewModel.uiState.value.clients.size)
    }

    @Test
    fun `onEvent onDismissErrorDialog SHOULD hide error`() = runTest {
        // GIVEN
        val expectedError = "Error loading clients"
        coEvery { clientRepository.getClients() } returns Result.failure(Exception(expectedError))
        clientListViewModel = ClientListViewModel(
            navigationProvider = navigationProvider,
            clientRepository = clientRepository
        )
        // WHEN
        clientListViewModel.onEvent(ClientListViewModel.UserEvent.OnDismissErrorDialog)
        // then
        assertFalse(clientListViewModel.uiState.value.showError)
    }

    @Test
    fun `onEvent OnClientOrderClicked SHOULD navigate to ClientOrders destination`() = runTest {
        // GIVEN
        val client = CLIENT_LIST.first()

        // WHEN
        clientListViewModel.onEvent(ClientListViewModel.UserEvent.OnClientOrderClicked(client))

        // THEN
        val slot = slot<AppDestination>()
        coVerify { navigationProvider.requestDestination(capture(slot), AppDestination.NewOrder.REQUEST_CODE) }
        val destination = slot.captured
        assertTrue(destination is AppDestination.NewOrder)
        assertEquals(client, destination.client)
    }
}
