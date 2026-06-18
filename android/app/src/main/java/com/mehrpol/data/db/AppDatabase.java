package com.mehrpol.data.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ScanResultEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ScanResultDao scanResultDao();

    private static volatile AppDatabase instance;

    public static AppDatabase get(Context context) {
        AppDatabase current = instance;
        if (current != null) return current;
        synchronized (AppDatabase.class) {
            current = instance;
            if (current == null) {
                current = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "mehrpol.db"
                ).build();
                instance = current;
            }
            return current;
        }
    }
}
