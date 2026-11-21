package com.farmsync.FarmSyncBackend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record FarmDetails(
        @JsonProperty("farm_id") String farmId,
        @JsonProperty("user_id") String userId,
        @JsonProperty("farm_name") @NotBlank(message = "farm_name is required") String farmName,
        @JsonProperty("crop_type") @NotBlank(message = "crop_type is required") String cropType,
        @JsonProperty("latitude") @NotNull(message = "latitude is required") Double latitude,
        @JsonProperty("longitude") @NotNull(message = "longitude is required") Double longitude,
        @JsonProperty("is_active") Boolean isActive,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt
) {
    public FarmDetails {
        Instant now = Instant.now();
        createdAt = (createdAt != null) ? createdAt : now;
        updatedAt = (updatedAt != null) ? updatedAt : now;
    }

    public FarmDetails forCreate(String userId) {
        Instant now = Instant.now();
        String resolvedId = (this.farmId == null || this.farmId.isBlank())
                ? UUID.randomUUID().toString()
                : this.farmId;
        Instant resolvedCreatedAt = (this.createdAt != null) ? this.createdAt : now;
        Instant resolvedUpdatedAt = (this.updatedAt != null) ? this.updatedAt : now;
        return new FarmDetails(
                resolvedId,
                userId,
                this.farmName,
                this.cropType,
                this.latitude,
                this.longitude,
                true,
                resolvedCreatedAt,
                resolvedUpdatedAt
        );
    }
    public FarmDetails forUpdate(String existingFarmId, Instant existingCreatedAt) {
        Instant now = Instant.now();
        String resolvedId = (existingFarmId != null && !existingFarmId.isBlank())
                ? existingFarmId
                : this.farmId;
        Instant resolvedCreatedAt = (this.createdAt != null)
                ? this.createdAt
                : (existingCreatedAt != null ? existingCreatedAt : now);
        Instant resolvedUpdatedAt = (this.updatedAt != null) ? this.updatedAt : now;
        return new FarmDetails(
                resolvedId,
                this.userId,
                this.farmName,
                this.cropType,
                this.latitude,
                this.longitude,
                this.isActive,
                resolvedCreatedAt,
                resolvedUpdatedAt
        );
    }
}