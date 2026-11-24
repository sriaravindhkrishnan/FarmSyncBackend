package com.farmsync.FarmSyncBackend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record RainForecastSlot(
        String slotId,
        String farmId,
        LocalDate forecastDate,
        LocalDateTime slotTime,
        Double rainProbability,
        String status,
        String createdAt,
        String updatedAt
) {
}