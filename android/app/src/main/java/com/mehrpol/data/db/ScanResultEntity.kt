package com.mehrpol.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_results")
data class ScanResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ip: String,
    val latency: Long,
    val timestamp: Long = System.currentTimeMillis()
)
