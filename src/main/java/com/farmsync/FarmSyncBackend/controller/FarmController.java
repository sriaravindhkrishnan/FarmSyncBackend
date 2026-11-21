package com.farmsync.FarmSyncBackend.controller;

import com.farmsync.FarmSyncBackend.config.ConfigValues;
import com.farmsync.FarmSyncBackend.model.FarmDetails;
import com.farmsync.FarmSyncBackend.service.FarmService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/farm")
public class FarmController {

    private final FarmService farmService;
    private final ConfigValues configValues;

    public  FarmController(FarmService farmService, ConfigValues configValues) {
        this.farmService = farmService;
        this.configValues = configValues;
    }

    @PostMapping
    public ResponseEntity<String> createFarmData(@RequestBody FarmDetails farmDetails) {
        farmDetails = farmDetails.forCreate(configValues.getGlobalUserId());
        boolean result = farmService.create(farmDetails);
        if (result) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Created Farm");
        }
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Failed to create Farm");
    }

    @GetMapping
    public ResponseEntity<List<FarmDetails>> getFarms() {
        String userID = configValues.getGlobalUserId();
        List<FarmDetails> farmDetails = farmService.getFarms(userID);
        return ResponseEntity.status(HttpStatus.OK).body(farmDetails);
    }
}
