package com.farmsync.FarmSyncBackend.scheduler;

import com.farmsync.FarmSyncBackend.config.ConfigValues;
import com.farmsync.FarmSyncBackend.model.FarmDetails;
import com.farmsync.FarmSyncBackend.model.WeatherAPIRegularResponse;
import com.farmsync.FarmSyncBackend.model.WeatherCheck;
import com.farmsync.FarmSyncBackend.service.FarmService;
import com.farmsync.FarmSyncBackend.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class WeatherCheckScheduler {

    private final WeatherService weatherService;
    private final FarmService farmService;
    private final ConfigValues configValues;

    public WeatherCheckScheduler(WeatherService weatherService, FarmService farmService,  ConfigValues configValues) {
        this.farmService = farmService;
        this.weatherService = weatherService;
        this.configValues = configValues;
    }


    @Scheduled(cron = "${weather-regular-cron}", zone = "${weather-timezone}")
    public synchronized void triggerRegularWeatherCheck(){
        System.out.println("Starting WeatherCheckScheduler triggerRegularWeatherCheck in time " + (System.currentTimeMillis() / 1000));
//        12.962976453605869, 80.20849426569832 => madipakkam
        WeatherCheck weatherCheck = new WeatherCheck("12.962976453605869", "80.20849426569832");
//        WeatherCheck weatherCheck = new WeatherCheck("-80.1373", "26.1224");
        WeatherAPIRegularResponse response = weatherService.getWeatherRegularInfo(weatherCheck);
        System.out.println(response);
        System.out.println("Completed WeatherCheckScheduler triggerRegularWeatherCheck in time " + (System.currentTimeMillis() / 1000));
    }

    private void start(){
        String userID = configValues.getGlobalUserId();
        List<FarmDetails> farmDetails = farmService.getFarms(userID);
        farmDetails.forEach(farmDetail -> {
            weatherService.getWeatherRegularInfo(farmDetails);
        });
    }
}
