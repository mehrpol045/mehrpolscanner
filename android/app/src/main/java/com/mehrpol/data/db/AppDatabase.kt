package com.mehrpol.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ScanResultEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scanResultDao(): ScanResultDao
}
