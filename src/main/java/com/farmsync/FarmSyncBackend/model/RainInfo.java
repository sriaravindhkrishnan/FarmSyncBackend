package com.farmsync.FarmSyncBackend.model;
import java.util.List;

public record RainInfo(
        double latitude,
        double longitude,
        boolean isRaining,
        String condition,
        double rainMm,
        List<DailyRain> forecast,
        WeatherResponse.Current current) {

    public record DailyRain(String date, double rainMm, int probabilityPercent) {
    }
}