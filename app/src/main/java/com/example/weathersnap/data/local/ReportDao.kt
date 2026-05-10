package com.example.weathersnap.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for [WeatherReportEntity].
 * All queries are observable via Flow (reactive) or suspend (one-shot on IO thread).
 */
@Dao
interface ReportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: WeatherReportEntity)

    @Query("SELECT * FROM weather_reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<WeatherReportEntity>>

    @Query("SELECT * FROM weather_reports WHERE id = :id")
    fun getReportById(id: String): Flow<WeatherReportEntity?>


    @Query("DELETE FROM weather_reports WHERE id = :id")
    suspend fun deleteReportById(id: String)
}
