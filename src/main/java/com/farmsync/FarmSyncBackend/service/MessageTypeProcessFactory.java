package com.farmsync.FarmSyncBackend.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MessageTypeProcessFactory {

    private final Map<String, MessageListenerFactory> processMap = new HashMap<>();

    public MessageTypeProcessFactory(List<MessageListenerFactory> processes) {
        System.out.println("Creating MessageTypeProcessFactory " + processes);
        for (MessageListenerFactory process : processes) {
            processMap.put(process.getType(), process);
        }
    }

    public MessageListenerFactory getProcess(String type) {
        System.out.println("Getting process for: " + processMap);
        MessageListenerFactory processor = processMap.get(type);
        if (processor == null) {
            throw new IllegalArgumentException("Unsupported request type: " + type);
        }
        return processor;
    }
}