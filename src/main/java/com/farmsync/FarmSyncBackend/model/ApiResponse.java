package com.farmsync.FarmSyncBackend.model;

public record ApiResponse<T>(
        boolean success,
        String message,
        T data
) {
}