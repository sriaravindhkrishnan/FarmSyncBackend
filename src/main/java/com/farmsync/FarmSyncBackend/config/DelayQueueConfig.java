package com.farmsync.FarmSyncBackend.config;
import java.util.HashMap;
import java.util.Map;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DelayQueueConfig {

    private final RabbitMqProperties props;

    public DelayQueueConfig(RabbitMqProperties props) {
        this.props = props;
    }

    @Bean
    public Queue readyQueue() {
        return new Queue(props.readyQueue(), true);
    }

    @Bean
    public Queue delayQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", props.delayTtlMs());
        args.put("x-dead-letter-exchange", props.readyExchange());
        args.put("x-dead-letter-routing-key", props.readyRoutingKey());
        return new Queue(props.delayQueue(), true, false, false, args);
    }
}