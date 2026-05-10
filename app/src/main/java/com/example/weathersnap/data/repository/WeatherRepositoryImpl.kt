package com.example.weathersnap.data.repository

import com.example.weathersnap.data.local.ReportDao
import com.example.weathersnap.data.local.WeatherReportEntity
import com.example.weathersnap.data.remote.WeatherApiService
import com.example.weathersnap.domain.model.WeatherData
import com.example.weathersnap.domain.model.weatherCodeToCondition
import com.example.weathersnap.domain.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService,
    private val reportDao: ReportDao
) : WeatherRepository {

    companion object {
        private const val GEOCODING_URL =
            "https://geocoding-api.open-meteo.com/v1/search"
        private const val FORECAST_URL =
            "https://api.open-meteo.com/v1/forecast"

        /** Comma-separated current weather variables requested from Open-Meteo */
        private const val CURRENT_PARAMS =
            "temperature_2m,relative_humidity_2m,wind_speed_10m,surface_pressure,weather_code"
    }

    /**
     * Geocodes [cityName] then fetches current weather.
     * All network I/O runs on [Dispatchers.IO].
     */
    override suspend fun getWeatherByCity(cityName: String): Result<WeatherData> =
        withContext(Dispatchers.IO) {
            runCatching {
                // Step 1: Resolve city to coordinates
                val geoResponse = apiService.searchLocation(
                    url = GEOCODING_URL,
                    name = cityName
                )
                val location = geoResponse.results?.firstOrNull()
                    ?: throw Exception("City not found: $cityName")

                // Step 2: Fetch weather for those coordinates
                val forecastResponse = apiService.getCurrentWeather(
                    url = FORECAST_URL,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    current = CURRENT_PARAMS
                )

                val current = forecastResponse.current

                WeatherData(
                    cityName = location.name,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    temperature = current.temperature,
                    weatherCode = current.weatherCode,
                    condition = weatherCodeToCondition(current.weatherCode),
                    humidity = current.humidity,
                    windSpeed = current.windSpeed,
                    pressure = current.pressure
                )
            }
        }

    /** Calls geocoding-only endpoint to return up to [count] city name suggestions. */
    override suspend fun searchCitySuggestions(query: String, count: Int): Result<List<String>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = apiService.searchLocation(
                    url   = GEOCODING_URL,
                    name  = query,
                    count = count
                )
                response.results?.map { r ->
                    buildString {
                        append(r.name)
                        r.admin1?.let { append(", $it") }
                        r.country?.let { append(", $it") }
                    }
                } ?: emptyList()
            }
        }

    /** Reactive stream of all reports, newest first. Room emits on every DB change. */
    override fun getAllReports(): Flow<List<WeatherReportEntity>> =
        reportDao.getAllReports()

    /** Reactive stream for a single report by UUID. */
    override fun getReportById(id: String): Flow<WeatherReportEntity?> =
        reportDao.getReportById(id)

    /** Persists a report on the IO dispatcher. */
    override suspend fun saveReport(report: WeatherReportEntity) =
        withContext(Dispatchers.IO) {
            reportDao.insertReport(report)
        }

    /** Removes a report on the IO dispatcher. */
    override suspend fun deleteReport(id: String) =
        withContext(Dispatchers.IO) {
            reportDao.deleteReportById(id)
        }
}
