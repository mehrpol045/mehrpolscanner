package com.mehrpol.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;
import kotlinx.coroutines.flow.Flow;

@Dao
public interface ScanResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBlocking(ScanResultEntity result);

    @Query("SELECT * FROM scan_results ORDER BY createdAt DESC LIMIT :limit")
    Flow<List<ScanResultEntity>> observeRecent(int limit);

    @Query("DELETE FROM scan_results WHERE id NOT IN (SELECT id FROM scan_results ORDER BY createdAt DESC LIMIT :keep)")
    void trimBlocking(int keep);
}
