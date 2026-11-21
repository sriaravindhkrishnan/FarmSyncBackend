package com.farmsync.FarmSyncBackend.model;

import java.time.Instant;
import java.time.LocalDate;

public record CreateRainForecastRequest(
        String farmId,
        LocalDate forecastDate,
        Instant slotTime,
        Double rainProbability
) {
}