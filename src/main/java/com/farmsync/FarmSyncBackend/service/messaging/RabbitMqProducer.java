package com.farmsync.FarmSyncBackend.service.messaging;

import com.farmsync.FarmSyncBackend.config.RabbitMqProperties;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitMqProducer {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqProperties props;

    public RabbitMqProducer(RabbitTemplate rabbitTemplate, RabbitMqProperties props) {
        this.rabbitTemplate = rabbitTemplate;
        this.props = props;
    }

    public void scheduleWeatherCheck(String payload) {
        rabbitTemplate.convertAndSend("", props.delayQueue(), payload, message -> {
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        });
    }

    public void scheduleWeatherCheckWithDelay(String payload, long delayMs) {
        rabbitTemplate.convertAndSend("", props.delayQueue(), payload, message -> {
            message.getMessageProperties().setExpiration(String.valueOf(delayMs));
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        });
    }
}