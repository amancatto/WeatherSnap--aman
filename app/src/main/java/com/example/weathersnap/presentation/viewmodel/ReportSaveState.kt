package com.example.weathersnap.presentation.viewmodel

/**
 * Represents every possible state of a report save operation.
 */
sealed class ReportSaveState {
    /** No save has been triggered yet (or was reset after completion). */
    object Idle : ReportSaveState()

    /** Room insert is in-progress. */
    object Saving : ReportSaveState()

    /** Insert completed successfully. */
    object Success : ReportSaveState()

    /** Insert failed with a reason. */
    data class Error(val message: String) : ReportSaveState()
}
