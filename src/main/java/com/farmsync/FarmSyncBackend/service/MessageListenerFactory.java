package com.farmsync.FarmSyncBackend.service;

import com.farmsync.FarmSyncBackend.model.RabbitMQConsumedMessage;

public interface MessageListenerFactory {
    String getType();
    void process(RabbitMQConsumedMessage payload);
}
