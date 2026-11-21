package com.farmsync.FarmSyncBackend.controller;

import com.farmsync.FarmSyncBackend.model.ApiResponse;
import com.farmsync.FarmSyncBackend.model.CreateRainForecastRequest;
import com.farmsync.FarmSyncBackend.model.RainForecastSlot;
import com.farmsync.FarmSyncBackend.service.RainForecastService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weather/slots")
public class RainForecastController {

    private final RainForecastService service;

    public RainForecastController(RainForecastService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RainForecastSlot>> createSlot(
            @RequestBody CreateRainForecastRequest request
    ) {
        RainForecastSlot slot = service.createSlot(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        "Rain forecast slot created successfully",
                        slot
                ));
    }

    @GetMapping("/{farmId}")
    public ResponseEntity<ApiResponse<List<RainForecastSlot>>> getSlots(
            @PathVariable String farmId
    ) {

        List<RainForecastSlot> slots =
                service.getSlotsByFarmId(farmId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Rain forecast slots retrieved successfully",
                        slots
                )
        );
    }

    @GetMapping("/{farmId}/pending")
    public ResponseEntity<ApiResponse<List<RainForecastSlot>>> getPendingSlots(
            @PathVariable String farmId
    ) {

        List<RainForecastSlot> slots =
                service.getPendingSlots(farmId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Pending rain forecast slots retrieved successfully",
                        slots
                )
        );
    }
}