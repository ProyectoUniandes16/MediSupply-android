package com.uniandes.medisupply.presentation.viewmodel.vendor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniandes.medisupply.domain.model.VisitStatus
import com.uniandes.medisupply.domain.repository.VendorRepository
import com.uniandes.medisupply.presentation.model.VisitStatusUI
import com.uniandes.medisupply.presentation.model.VisitUI
import com.uniandes.medisupply.presentation.model.toUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class VisitUiState(
    val selectedDate: String,
    val visitList: List<VisitUI> = emptyList(),
    val isLoading: Boolean = true,
    val showError: Boolean = false,
    val errorMessage: String? = null
)
class VisitListViewmodel(
    private val vendorRepository: VendorRepository
) : ViewModel() {

    private var visitList: List<VisitUI> = emptyList()
    private val today = Date()
    private val dateRange = List(10) { index ->
        val calendar = Calendar.getInstance()
        calendar.time = today
        calendar.add(Calendar.DAY_OF_MONTH, index)
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
    }
    private val _uiState = MutableStateFlow(
        VisitUiState(
            selectedDate = dateRange[0],
        )
    )

    val uiState = _uiState.asStateFlow()

    private fun loadVisits() {
        _uiState.update { it.copy(isLoading = true, showError = false, errorMessage = null) }
        viewModelScope.launch {
            vendorRepository.getVisits(
                dateRange.first(), dateRange.last()
            ).onSuccess {
                visitList = it.map { v -> v.toUi() }
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        visitList = filterVisitsByDateAndMarkStarted(state.selectedDate)
                    )
                }
            }.onFailure {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        showError = true,
                        errorMessage = it.message
                    )
                }
            }
        }
    }

    fun onEvent(event: UserEvent) {
        when (event) {
            is UserEvent.OnScreenLoaded -> {
                loadVisits()
            }

            is UserEvent.OnForwardDateClicked -> {
                val currentIndex = dateRange.indexOf(_uiState.value.selectedDate)
                if (currentIndex < dateRange.size - 1) {
                    val newDate = dateRange[currentIndex + 1]
                    _uiState.update { state ->
                        state.copy(
                            selectedDate = newDate,
                            visitList = filterVisitsByDateAndMarkStarted(newDate)
                        )
                    }
                }
            }

            is UserEvent.OnBackwardDateClicked -> {
                val currentIndex = dateRange.indexOf(_uiState.value.selectedDate)
                if (currentIndex > 0) {
                    val newDate = dateRange[currentIndex - 1]
                    _uiState.update { state ->
                        state.copy(
                            selectedDate = newDate,
                            visitList = filterVisitsByDateAndMarkStarted(newDate)
                        )
                    }
                }
            }
            is UserEvent.OnErrorDialogDismissed -> {
                _uiState.update { state ->
                    state.copy(
                        showError = false,
                        errorMessage = null
                    )
                }
                loadVisits()
            }
            is UserEvent.OnUpdateVisitClicked -> {
                updateVisitStatus(event.visit.id, event.visit.status)
            }
        }
    }

    private fun filterVisitsByDateAndMarkStarted(date: String): List<VisitUI> {
        var isPendingOrInProgressAny = false
        val list = visitList.filter {
            it.visitDate == date
        }.map {
           val item: VisitUI
            when (it.status) {
                VisitStatusUI.COMPLETED -> {
                    item = it.copy(canBeStarted = false)
                }
                else -> {
                    item = it.copy(canBeStarted = isPendingOrInProgressAny.not())
                    isPendingOrInProgressAny = true
                }
            }
            item
        }
        return list
    }

    private fun updateVisitStatus(visitId: Int, status: VisitStatusUI) {
        _uiState.update { it.copy(isLoading = true, showError = false, errorMessage = null) }
        viewModelScope.launch {
            val newVisitStatus = if (status == VisitStatusUI.PENDING) {
                VisitStatus.IN_PROGRESS
            } else {
                VisitStatus.COMPLETED
            }
            vendorRepository.updateVisitStatus(visitId, newVisitStatus)
                .onSuccess {
                    loadVisits()
                }
                .onFailure {
                    _uiState.update { state ->
                        state.copy(
                            showError = true,
                            errorMessage = it.message
                        )
                    }
                }
        }
    }

    sealed class UserEvent {
        data object OnScreenLoaded : UserEvent()
        data object OnForwardDateClicked : UserEvent()
        data object OnBackwardDateClicked : UserEvent()
        data object OnErrorDialogDismissed : UserEvent()
        data class OnUpdateVisitClicked(val visit: VisitUI) : UserEvent()
    }
}
