package com.farmsync.FarmSyncBackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rabbitmq")
public record RabbitMqProperties(
        String readyExchange,
        String readyQueue,
        String readyRoutingKey,
        String delayQueue,
        long delayTtlMs
) {}