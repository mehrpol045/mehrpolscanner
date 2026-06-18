package com.mehrpol.data.db;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "scan_results",
    indices = {@Index(value = {"ip", "port", "createdAt"})}
)
public class ScanResultEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String ip;
    public int port;
    public int latencyMs;
    public double loss;
    public String colo;
    public boolean isHealthy;
    public boolean isPhase2;
    public String phase2Type;
    public double phase2Speed;
    public boolean phase2Status;
    public int jitterMs;
    public String datacenterName;
    public String countryCode;
    public String region;
    public long createdAt;

    public ScanResultEntity() {
    }

    public ScanResultEntity(
        String ip,
        int port,
        int latencyMs,
        double loss,
        String colo,
        boolean isHealthy,
        boolean isPhase2,
        String phase2Type,
        double phase2Speed,
        boolean phase2Status,
        int jitterMs,
        String datacenterName,
        String countryCode,
        String region,
        long createdAt
    ) {
        this.ip = ip;
        this.port = port;
        this.latencyMs = latencyMs;
        this.loss = loss;
        this.colo = colo;
        this.isHealthy = isHealthy;
        this.isPhase2 = isPhase2;
        this.phase2Type = phase2Type;
        this.phase2Speed = phase2Speed;
        this.phase2Status = phase2Status;
        this.jitterMs = jitterMs;
        this.datacenterName = datacenterName;
        this.countryCode = countryCode;
        this.region = region;
        this.createdAt = createdAt;
    }
}
