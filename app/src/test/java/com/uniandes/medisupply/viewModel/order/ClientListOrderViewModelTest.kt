package com.uniandes.medisupply.viewModel.order

import com.uniandes.medisupply.common.InternalNavigator
import com.uniandes.medisupply.domain.model.Client
import com.uniandes.medisupply.domain.model.ClientContactInfo
import com.uniandes.medisupply.domain.repository.ClientRepository
import com.uniandes.medisupply.presentation.navigation.Destination
import com.uniandes.medisupply.presentation.viewmodel.order.ClientListOrderViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ClientListOrderViewModelTest {

    private lateinit var viewModel: ClientListOrderViewModel
    private val clientRepository: ClientRepository = mockk(relaxed = true)
    private val internalNavigator: InternalNavigator = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() = runTest {
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `init SHOULD load clients`() {
        // GIVEN
        mockClients()

        // WHEN
        viewModel = ClientListOrderViewModel(
            clientRepository = clientRepository,
            internalNavigator = internalNavigator
        )
        // THEN
        assertEquals(false, viewModel.clientsUiState.value.isLoading)
        assertEquals(CLIENT_LIST, viewModel.clientsUiState.value.clients)
    }

    @Test
    fun `init SHOULD show error when clients repository fails AND then dismiss dialog`() {
        // GIVEN
        val errorMessage = "Error loading clients"
        mockClients(
            Result.failure(Exception(errorMessage))
        )

        // WHEN
        viewModel = ClientListOrderViewModel(
            clientRepository = clientRepository,
            internalNavigator = internalNavigator
        )
        // THEN
        assertEquals(false, viewModel.clientsUiState.value.isLoading)
        assertEquals(errorMessage, viewModel.clientsUiState.value.error)
        assertEquals(true, viewModel.clientsUiState.value.showError)

        // AND WHEN
        viewModel.onEvent(ClientListOrderViewModel.UserEvent.OnErrorDialogDismissed)

        // THEN
        assertEquals(false, viewModel.clientsUiState.value.showError)
    }

    @Test
    fun `onEvent OnBackClicked SHOULD finish current destination`() {
        // GIVEN
        mockClients()
        viewModel = ClientListOrderViewModel(
            clientRepository = clientRepository,
            internalNavigator = internalNavigator
        )

        // WHEN
        viewModel.onEvent(ClientListOrderViewModel.UserEvent.OnBackClicked)

        // THEN
        coVerify(exactly = 1) {
            internalNavigator.finishCurrentDestination()
        }
    }

    @Test
    fun `onEvent OnClientClicked SHOULD navigate to CreateOrder with client`() {
        // GIVEN
        mockClients()
        viewModel = ClientListOrderViewModel(
            clientRepository = clientRepository,
            internalNavigator = internalNavigator
        )
        val client = CLIENT

        // WHEN
        viewModel.onEvent(ClientListOrderViewModel.UserEvent.OnClientClicked(client))

        // THEN
        coVerify(exactly = 1) {
            internalNavigator.navigateTo(
                Destination.CreateOrder,
                mapOf(Destination.CreateOrder.CLIENT to client)
            )
        }
    }

    private fun mockClients(
        result: Result<List<Client>> = Result.success(CLIENT_LIST)
    ) {
        coEvery { clientRepository.getClients() }.returns(result)
    }

    private companion object {
        val CONTACT_INFO = ClientContactInfo(
            phone = "1234567890",
            email = "coreo@corre.com",
            name = "contact name",
            position = "Manager"
        )
        val CLIENT = Client(
            id = 1,
            name = "John Doe",
            email = "correo@correo.com",
            address = "123 Main St",
            contactInfo = CONTACT_INFO)

        val CLIENT_LIST = List(10) { index ->
            CLIENT.copy(id = index + 1, name = "Client $index")
        }
    }
}
