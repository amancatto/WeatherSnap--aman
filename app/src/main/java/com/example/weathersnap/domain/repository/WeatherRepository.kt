package com.example.weathersnap.domain.repository

import com.example.weathersnap.data.local.WeatherReportEntity
import com.example.weathersnap.domain.model.WeatherData
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    suspend fun getWeatherByCity(cityName: String): Result<WeatherData>
    suspend fun searchCitySuggestions(query: String, count: Int = 5): Result<List<String>>
    fun getAllReports(): Flow<List<WeatherReportEntity>>
    fun getReportById(id: String): Flow<WeatherReportEntity?>
    suspend fun saveReport(report: WeatherReportEntity)
    suspend fun deleteReport(id: String)
}
