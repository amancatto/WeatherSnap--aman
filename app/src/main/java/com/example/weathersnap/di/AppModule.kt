package com.example.weathersnap.di

import android.content.Context
import androidx.room.Room
import com.example.weathersnap.data.local.ReportDao
import com.example.weathersnap.data.local.WeatherDatabase
import com.example.weathersnap.data.remote.WeatherApiService
import com.example.weathersnap.data.repository.WeatherRepositoryImpl
import com.example.weathersnap.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ─────────────────────────────────────────────
    // Network
    // ─────────────────────────────────────────────

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

    /**
     * Base URL is a placeholder — actual URLs are supplied via @Url in [WeatherApiService].
     * This enables calling two different Open-Meteo domains from a single Retrofit instance.
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideWeatherApiService(retrofit: Retrofit): WeatherApiService =
        retrofit.create(WeatherApiService::class.java)

    // ─────────────────────────────────────────────
    // Database
    // ─────────────────────────────────────────────

    @Provides
    @Singleton
    fun provideWeatherDatabase(@ApplicationContext context: Context): WeatherDatabase =
        Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            "weather_snap_db"
        ).build()

    @Provides
    @Singleton
    fun provideReportDao(database: WeatherDatabase): ReportDao =
        database.reportDao()

    // ─────────────────────────────────────────────
    // Repository
    // ─────────────────────────────────────────────

    @Provides
    @Singleton
    fun provideWeatherRepository(
        apiService: WeatherApiService,
        reportDao: ReportDao
    ): WeatherRepository = WeatherRepositoryImpl(apiService, reportDao)
}
