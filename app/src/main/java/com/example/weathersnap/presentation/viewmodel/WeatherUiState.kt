package com.example.weathersnap.presentation.viewmodel

import com.example.weathersnap.domain.model.WeatherData

/**
 * Represents every possible state of the Weather search screen.
 */
sealed class WeatherUiState {
    /** No search has been performed yet. */
    object Empty : WeatherUiState()

    /** A network request is in-flight. */
    object Loading : WeatherUiState()

    /** Weather data successfully fetched. */
    data class Success(val data: WeatherData) : WeatherUiState()

    /** A network or geocoding error occurred. */
    data class Error(val message: String) : WeatherUiState()
}
