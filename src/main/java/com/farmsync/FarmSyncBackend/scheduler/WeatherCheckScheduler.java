package com.farmsync.FarmSyncBackend.scheduler;

import com.farmsync.FarmSyncBackend.model.WeatherAPIRegularResponse;
import com.farmsync.FarmSyncBackend.model.WeatherCheck;
import com.farmsync.FarmSyncBackend.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WeatherCheckScheduler {

    @Autowired
    private WeatherService weatherService;

    @Scheduled(cron = "${weather-regular-cron}", zone = "${weather-timezone}")
    public synchronized void triggerRegularWeatherCheck(){
        System.out.println("Starting WeatherCheckScheduler triggerRegularWeatherCheck in time " + (System.currentTimeMillis() / 1000));
//        WeatherCheck weatherCheck = new WeatherCheck("12.962999", "80.208460");
        WeatherCheck weatherCheck = new WeatherCheck("-80.1373", "26.1224");
        WeatherAPIRegularResponse response = weatherService.getWeatherRegularInfo(weatherCheck);
        System.out.println(response);
        System.out.println("Completed WeatherCheckScheduler triggerRegularWeatherCheck in time " + (System.currentTimeMillis() / 1000));
    }
}
