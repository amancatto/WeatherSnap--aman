package com.example.weathersnap.presentation.viewmodel

sealed class ReportSaveState {
    object Idle : ReportSaveState()
    object Saving : ReportSaveState()
    object Success : ReportSaveState()
    data class Error(val message: String) : ReportSaveState()
}
