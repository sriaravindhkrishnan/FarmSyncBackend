package com.farmsync.FarmSyncBackend.model;
import java.util.List;

public record RainInfo(
        double latitude,
        double longitude,
        boolean isRaining,
        String condition,
        double rainMm,
        String formName,
        String formId,
        WeatherResponse.Current current) {
}