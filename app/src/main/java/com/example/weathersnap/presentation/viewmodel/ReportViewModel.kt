package com.example.weathersnap.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathersnap.data.local.WeatherReportEntity
import com.example.weathersnap.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for both the Create Report screen and the History (list) screen.
 *
 * Responsibilities:
 *  - Persists a [WeatherReportEntity] to Room via the IO dispatcher.
 *  - Exposes all saved reports as a hot [StateFlow] backed by Room's Flow.
 *  - Exposes [ReportSaveState] so the UI can react to save success/failure.
 */
@HiltViewModel
class ReportViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    // ─── Save State ───────────────────────────────────────────────────────────

    private val _saveState = MutableStateFlow<ReportSaveState>(ReportSaveState.Idle)
    val saveState: StateFlow<ReportSaveState> = _saveState.asStateFlow()

    // ─── All Reports (reactive) ───────────────────────────────────────────────

    /**
     * Hot StateFlow of all saved reports, sourced from Room's Flow.
     * Uses [SharingStarted.WhileSubscribed] with a 5-second timeout so the
     * upstream DB query stays active during config changes.
     */
    val allReports: StateFlow<List<WeatherReportEntity>> = repository
        .getAllReports()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS),
            initialValue = emptyList()
        )

    // ─── Public API ───────────────────────────────────────────────────────────

    /**
     * Saves [report] to Room on the IO dispatcher.
     * Updates [saveState] so the UI can show a success/error indicator.
     */
    fun saveReport(report: WeatherReportEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            _saveState.value = ReportSaveState.Saving
            try {
                repository.saveReport(report)
                _saveState.value = ReportSaveState.Success
            } catch (e: Exception) {
                _saveState.value = ReportSaveState.Error(
                    e.message ?: "Failed to save report"
                )
            }
        }
    }

    /**
     * Deletes a report by its UUID on the IO dispatcher.
     */
    fun deleteReport(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteReport(id)
        }
    }

    /**
     * Resets save state to [ReportSaveState.Idle] after the UI has consumed the result.
     */
    fun resetSaveState() {
        _saveState.value = ReportSaveState.Idle
    }

    // ─── Constants ────────────────────────────────────────────────────────────

    companion object {
        private const val SUBSCRIPTION_TIMEOUT_MS = 5_000L
    }
}
