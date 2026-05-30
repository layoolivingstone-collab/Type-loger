package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DialLogDao {
    @Query("SELECT * FROM dial_logs ORDER BY timestamp DESC")
    fun getAllLogsFlow(): Flow<List<DialLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: DialLog): Long

    @Update
    suspend fun updateLog(log: DialLog)

    @Query("DELETE FROM dial_logs WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM dial_logs")
    suspend fun deleteAllLogs()
}
