package com.example.weathersnap.domain.repository

import com.example.weathersnap.data.local.WeatherReportEntity
import com.example.weathersnap.domain.model.WeatherData
import kotlinx.coroutines.flow.Flow

/**
 * Contract for all data operations.
 * The domain layer depends only on this interface — never on the implementation.
 */
interface WeatherRepository {

    /**
     * Fetches live weather for a city name via geocoding + forecast APIs.
     */
    suspend fun getWeatherByCity(cityName: String): Result<WeatherData>

    /**
     * Returns up to [count] city name suggestions for partial [query] (min 2 chars).
     * Used for autocomplete in the search bar.
     */
    suspend fun searchCitySuggestions(query: String, count: Int = 5): Result<List<String>>

    /**
     * Returns a reactive stream of all saved weather reports, ordered by newest first.
     */
    fun getAllReports(): Flow<List<WeatherReportEntity>>

    /**
     * Returns a reactive stream for a single report by its UUID.
     */
    fun getReportById(id: String): Flow<WeatherReportEntity?>

    /**
     * Persists a completed weather snapshot report to the local database.
     */
    suspend fun saveReport(report: WeatherReportEntity)

    /**
     * Removes a report by its UUID.
     */
    suspend fun deleteReport(id: String)
}
