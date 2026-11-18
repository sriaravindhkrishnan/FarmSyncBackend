package com.farmsync.FarmSyncBackend.controller;

import com.farmsync.FarmSyncBackend.model.RainInfo;
import com.farmsync.FarmSyncBackend.model.WeatherCheck;
import com.farmsync.FarmSyncBackend.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wether")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @PostMapping
    public RainInfo wetherCheckByLocation(@RequestBody WeatherCheck wetherCheck) {
        return weatherService.getRainInfo(wetherCheck);
    }
}
