package com.farmsync.FarmSyncBackend.consumer;


import com.farmsync.FarmSyncBackend.model.RabbitMQConsumedMessage;
import com.farmsync.FarmSyncBackend.service.MessageListenerFactory;
import com.farmsync.FarmSyncBackend.service.MessageTypeProcessFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class WeatherCheckListener {
    private final ObjectMapper objectMapper;
    private final MessageTypeProcessFactory messageTypeProcessFactory;

    public WeatherCheckListener(ObjectMapper objectMapper, MessageTypeProcessFactory messageTypeProcessFactory) {
        this.objectMapper = objectMapper;
        this.messageTypeProcessFactory = messageTypeProcessFactory;
    }

    @RabbitListener(queues = "${app.rabbitmq.ready-queue}")
    public void handleWeatherCheck(String msg) {
        try{
            System.out.println("Received payload from message queue: " + msg);
            RabbitMQConsumedMessage payload = objectMapper.readValue(msg, RabbitMQConsumedMessage.class);
            System.out.println("Received request for: " + payload.getRequestType());
            MessageListenerFactory processor = messageTypeProcessFactory.getProcess(payload.getRequestType());
            processor.process(payload);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Received Wrong message payload: " + msg);
        }


    }
}