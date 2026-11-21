package com.farmsync.FarmSyncBackend.service;

import com.farmsync.FarmSyncBackend.model.CreateRainForecastRequest;
import com.farmsync.FarmSyncBackend.model.RainForecastSlot;
import com.farmsync.FarmSyncBackend.repository.RainForecastRepository;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class RainForecastService {

    private final RainForecastRepository repository;

    public RainForecastService(RainForecastRepository repository) {
        this.repository = repository;
    }

    public RainForecastSlot createSlot(
            CreateRainForecastRequest request
    ) {

        Instant now = Instant.now();

        RainForecastSlot slot = new RainForecastSlot(
                UUID.randomUUID().toString(),
                request.farmId(),
                request.forecastDate(),
                request.slotTime(),
                request.rainProbability(),
                "PENDING",
                now,
                now
        );

        repository.save(slot);

        return slot;
    }

    public List<RainForecastSlot> getSlotsByFarmId(String farmId) {
        return repository.findByFarmId(farmId);
    }

    public List<RainForecastSlot> getPendingSlots(String farmId) {
        return repository.findPendingSlots(farmId);
    }
}