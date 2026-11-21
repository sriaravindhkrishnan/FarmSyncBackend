package com.farmsync.FarmSyncBackend.model;

import java.time.Instant;
import java.time.LocalDate;

public record RainForecastSlot(
        String slotId,
        String farmId,
        LocalDate forecastDate,
        Instant slotTime,
        Double rainProbability,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
}