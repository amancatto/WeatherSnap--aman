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

    /**
     * Inserts or replaces a weather report.
     * Call from an IO dispatcher — Room enforces this.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: WeatherReportEntity)

    /**
     * Returns all reports sorted by newest first as a reactive Flow.
     */
    @Query("SELECT * FROM weather_reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<WeatherReportEntity>>

    /**
     * Returns a single report by UUID as a reactive Flow.
     */
    @Query("SELECT * FROM weather_reports WHERE id = :id")
    fun getReportById(id: String): Flow<WeatherReportEntity?>

    /**
     * Deletes a single report by its UUID.
     */
    @Query("DELETE FROM weather_reports WHERE id = :id")
    suspend fun deleteReportById(id: String)
}
