package com.example.weathersnap.domain.model

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
