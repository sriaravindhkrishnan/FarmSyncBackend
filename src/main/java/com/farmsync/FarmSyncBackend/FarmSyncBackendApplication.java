package com.farmsync.FarmSyncBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FarmSyncBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FarmSyncBackendApplication.class, args);
	}

}
