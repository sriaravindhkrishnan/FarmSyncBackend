package com.farmsync.FarmSyncBackend;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@EnableRabbit
@ConfigurationPropertiesScan
public class FarmSyncBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FarmSyncBackendApplication.class, args);
	}

}
