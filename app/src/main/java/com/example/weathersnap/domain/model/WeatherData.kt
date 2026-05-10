package com.example.weathersnap.domain.model

/**
 * Domain model representing the weather data fetched from the API.
 * This is the clean business model used across the presentation layer.
 */
data class WeatherData(
    val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val temperature: Double,
    val weatherCode: Int,
    val condition: String,
    val humidity: Int,
    val windSpeed: Double,
    val pressure: Double
)
