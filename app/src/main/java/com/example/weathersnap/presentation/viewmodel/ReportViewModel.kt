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

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    // Save State are here

    private val _saveState = MutableStateFlow<ReportSaveState>(ReportSaveState.Idle)
    val saveState: StateFlow<ReportSaveState> = _saveState.asStateFlow()

    //  Reports are here

    val allReports: StateFlow<List<WeatherReportEntity>> = repository
        .getAllReports()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS),
            initialValue = emptyList()
        )

    //Public api

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

    fun deleteReport(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteReport(id)
        }
    }

    fun resetSaveState() {
        _saveState.value = ReportSaveState.Idle
    }

    //  Constants

    companion object {
        private const val SUBSCRIPTION_TIMEOUT_MS = 5_000L
    }
}
