package com.mehrpol

import android.app.Application
import androidx.room.Room
import com.mehrpol.data.db.AppDatabase

class MehrpolApp : Application() {
    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java, "mehrpol-db"
        ).build()
    }
}
