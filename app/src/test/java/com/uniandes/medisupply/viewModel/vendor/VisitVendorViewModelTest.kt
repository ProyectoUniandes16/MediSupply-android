package com.uniandes.medisupply.viewModel.vendor

import com.uniandes.medisupply.companion.CLIENT
import com.uniandes.medisupply.domain.model.Visit
import com.uniandes.medisupply.domain.repository.VendorRepository
import com.uniandes.medisupply.presentation.viewmodel.vendor.VisitListViewmodel
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class VisitVendorViewModelTest {

    private lateinit var viewmodel: VisitListViewmodel
    private val vendorRepository = mockk<VendorRepository>(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewmodel = VisitListViewmodel(
            vendorRepository = vendorRepository
        )
    }

    @Test
    fun `onEvent OnScreenLoaded SHOULD load visits`() = runTest {
        // given
        coEvery {
            vendorRepository.getVisits(any(), any())
        } returns Result.success(TEST_VISIT_LIST)
        // when
        viewmodel.onEvent(VisitListViewmodel.UserEvent.OnScreenLoaded)
        // then
        assertEquals(
            viewmodel.uiState.value.visitList.size,
            TEST_VISIT_LIST.filter { it.visitDate == viewmodel.uiState.value.selectedDate }.size
        )
    }

    @Test
    fun `onEvent OnScreenLoaded SHOULD show error when loading fails and dismiss error should hide error and retry`() = runTest {
        // given
        val errorMessage = "Network error"
        coEvery {
            vendorRepository.getVisits(any(), any())
        } returns Result.failure(Exception(errorMessage))
        // when
        viewmodel.onEvent(VisitListViewmodel.UserEvent.OnScreenLoaded)
        // then
        assertEquals(
            viewmodel.uiState.value.errorMessage,
            errorMessage
        )
        assertTrue(viewmodel.uiState.value.showError)

        // and when dismissing error
        viewmodel.onEvent(VisitListViewmodel.UserEvent.OnErrorDialogDismissed)
        // then error should be hidden
        coVerify(exactly = 2) { vendorRepository.getVisits(any(), any()) }
    }

    @Test
    fun `onEvent OnForwardDateClicked SHOULD move to next date AND filter visits by date`() = runTest {
        // given
        coEvery {
            vendorRepository.getVisits(any(), any())
        } returns Result.success(TEST_VISIT_LIST)
        viewmodel.onEvent(VisitListViewmodel.UserEvent.OnScreenLoaded)
        // when
        viewmodel.onEvent(VisitListViewmodel.UserEvent.OnForwardDateClicked)
        // then
        assertEquals(
            viewmodel.uiState.value.visitList.size,
            TEST_VISIT_LIST.filter { it.visitDate == viewmodel.uiState.value.selectedDate }.size
        )
    }

    @Test
    fun `onEvent OnBackwardDateClicked SHOULD move to previous date AND filter visits by date`() = runTest {
        // given
        coEvery {
            vendorRepository.getVisits(any(), any())
        } returns Result.success(TEST_VISIT_LIST)
        viewmodel.onEvent(VisitListViewmodel.UserEvent.OnScreenLoaded)
        viewmodel.onEvent(VisitListViewmodel.UserEvent.OnForwardDateClicked)
        viewmodel.onEvent(VisitListViewmodel.UserEvent.OnBackwardDateClicked)
        // when
        viewmodel.onEvent(VisitListViewmodel.UserEvent.OnBackwardDateClicked)
        // then
        assertEquals(
            viewmodel.uiState.value.visitList.size,
            TEST_VISIT_LIST.filter { it.visitDate == viewmodel.uiState.value.selectedDate }.size
        )
    }

    @Test
    fun `onEvent OnBackwardDateClicked SHOULD not move to previous date if already first date`() = runTest {
        // given
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        coEvery {
            vendorRepository.getVisits(any(), any())
        } returns Result.success(TEST_VISIT_LIST)
        viewmodel.onEvent(VisitListViewmodel.UserEvent.OnScreenLoaded)
        // when
        viewmodel.onEvent(VisitListViewmodel.UserEvent.OnBackwardDateClicked)
        viewmodel.onEvent(VisitListViewmodel.UserEvent.OnBackwardDateClicked)
        // then
        assertEquals(
            viewmodel.uiState.value.selectedDate,
            today
        )
    }

    @Test
    fun `onEvent OnForwardDateClicked SHOULD not move to next date if already last date`() = runTest {
        // given
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val lastDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date().apply {
            time += 9 * 24 * 60 * 60 * 1000L
        })
        coEvery {
            vendorRepository.getVisits(any(), any())
        } returns Result.success(TEST_VISIT_LIST)
        viewmodel.onEvent(VisitListViewmodel.UserEvent.OnScreenLoaded)
        // when moving 20 days forward
        repeat(20) {
            viewmodel.onEvent(VisitListViewmodel.UserEvent.OnForwardDateClicked)
        }
        // then
        assertEquals(
            viewmodel.uiState.value.selectedDate,
            lastDate
        )
    }

    companion object {
        private val TEST_VISIT = Visit(
            status = "COMPLETED",
            visitDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
            client = CLIENT
        )
        val TEST_VISIT_LIST = List(10) {
            TEST_VISIT.copy(
                visitDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                    Date().apply {
                        time += it * 24 * 60 * 60 * 1000L
                    }
                )
            )
        }
    }
}
