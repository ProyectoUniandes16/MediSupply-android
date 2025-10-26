package com.uniandes.medisupply.viewModel

import com.uniandes.medisupply.common.AppDestination
import com.uniandes.medisupply.common.NavigationProvider
import com.uniandes.medisupply.presentation.viewmodel.ClientListViewModel
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
class ClientListViewModelTest {

    private lateinit var clientListViewModel: ClientListViewModel
    private val navigationProvider: NavigationProvider = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        clientListViewModel = ClientListViewModel(
            navigationProvider = navigationProvider
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onEvent  SHOULD update name`() = runTest {
        // when
        clientListViewModel.onEvent(ClientListViewModel.ClientListEvent.OnNewClientClick)

        // then
        val slot = slot<AppDestination>()
        val slotRequest = slot<Int>()
        // capture both arguments: the destination and the optional request code
        coVerify { navigationProvider.requestDestination(capture(slot), capture(slotRequest)) }
        assertTrue(slot.captured is AppDestination.NewClient)
        // request code should be null for this navigation call
        assertEquals(AppDestination.NewClient.REQUEST_CODE, slotRequest.captured)
    }
}
