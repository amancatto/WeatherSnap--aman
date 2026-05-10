package com.example.weathersnap.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey


  //Room entity representing a saved weather snapshot report.
  //Primary key id is a String not int

@Entity(tableName = "weather_reports")
data class WeatherReportEntity(

    @PrimaryKey
    val id: String,

    val cityName: String,

    val temperature: Double,

    val condition: String,

    val humidity: Int,

    val windSpeed: Double,

    val pressure: Double,

    val imagePath: String,

    val originalImageSize: Long,

    val compressedImageSize: Long,

    val notes: String = "",

    val timestamp: Long
)
