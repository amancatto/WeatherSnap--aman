package com.example.weathersnap.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * The single Room database for WeatherSnap.
 * exportSchema is false to avoid requiring schema export directory config.
 * Increment [version] whenever the schema changes.
 */
@Database(
    entities = [WeatherReportEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun reportDao(): ReportDao
}
