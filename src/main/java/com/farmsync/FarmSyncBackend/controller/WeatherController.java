package com.farmsync.FarmSyncBackend.controller;

import com.farmsync.FarmSyncBackend.model.RainInfo;
import com.farmsync.FarmSyncBackend.model.WeatherAPIRegularResponse;
import com.farmsync.FarmSyncBackend.model.WeatherCheck;
import com.farmsync.FarmSyncBackend.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wether")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @PostMapping
    public WeatherAPIRegularResponse wetherCheckByLocation(@RequestBody WeatherCheck wetherCheck) {
        return weatherService.getWeatherRegularInfo(wetherCheck);
    }

    @GetMapping("/health")
    public String health() {
        return "I am healthy and updated the real time rain monitor";
    }
}
