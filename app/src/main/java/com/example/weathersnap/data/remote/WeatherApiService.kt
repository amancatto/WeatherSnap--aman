package com.example.weathersnap.data.remote

import com.example.weathersnap.data.remote.dto.ForecastResponse
import com.example.weathersnap.data.remote.dto.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

 //full url is passed here so  to override this Retrofit base url below
    //- https://geocoding-api.open-meteo.com/v1/search
   // - https://api.open-meteo.com/v1/forecast

interface WeatherApiService {

    @GET
    suspend fun searchLocation(
        @Url url: String,
        @Query("name") name: String,
        @Query("count") count: Int = 1,
        @Query("language") language: String = "en",
        @Query("format") format: String = "json"
    ): GeocodingResponse

    @GET
    suspend fun getCurrentWeather(
        @Url url: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String,
        @Query("wind_speed_unit") windSpeedUnit: String = "kmh"
    ): ForecastResponse
}
