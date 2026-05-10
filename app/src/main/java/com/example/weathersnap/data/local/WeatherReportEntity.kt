package com.example.weathersnap.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a saved weather snapshot report.
 *
 * STRICT RULE: Primary key [id] is a String (UUID), never an Integer.
 */
@Entity(tableName = "weather_reports")
data class WeatherReportEntity(

    /** UUID string — generated via UUID.randomUUID().toString() before insertion */
    @PrimaryKey
    val id: String,

    /** City name resolved via Open-Meteo geocoding */
    val cityName: String,

    /** Temperature in degrees Celsius */
    val temperature: Double,

    /** Human-readable weather condition (e.g., "Clear Sky", "Moderate Rain") */
    val condition: String,

    /** Relative humidity percentage */
    val humidity: Int,

    /** Wind speed in km/h */
    val windSpeed: Double,

    /** Surface pressure in hPa */
    val pressure: Double,

    /** Absolute file path to the captured/stored image on device storage */
    val imagePath: String,

    /** Size of the original captured image in bytes */
    val originalImageSize: Long,

    /** Size of the compressed image in bytes */
    val compressedImageSize: Long,

    /** Optional free-text notes added by the user */
    val notes: String = "",

    /** Unix epoch timestamp in milliseconds when the report was created */
    val timestamp: Long
)
