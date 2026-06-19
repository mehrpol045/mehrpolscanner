package com.mehrpol.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanResultDao {
    @Insert
    suspend fun insert(result: ScanResultEntity)

    @Query("SELECT * FROM scan_results ORDER BY timestamp DESC")
    fun getAllResults(): Flow<List<ScanResultEntity>>
}
