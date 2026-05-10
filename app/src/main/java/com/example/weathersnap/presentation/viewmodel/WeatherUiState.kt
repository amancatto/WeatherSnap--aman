package com.example.weathersnap.presentation.viewmodel

import com.example.weathersnap.domain.model.WeatherData

sealed class WeatherUiState {
    object Empty : WeatherUiState()
    object Loading : WeatherUiState()
    data class Success(val data: WeatherData) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}
