package com.farmsync.FarmSyncBackend.controller;

import com.farmsync.FarmSyncBackend.model.RainInfo;
import com.farmsync.FarmSyncBackend.model.WeatherResponse;
import com.farmsync.FarmSyncBackend.service.RainAlertService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://192.168.1.5:5173"}) // your React dev URLs
public class RainAlertController {

    private final RainAlertService rainAlertService;

    public RainAlertController(RainAlertService rainAlertService) {
        this.rainAlertService = rainAlertService;
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        return rainAlertService.subscribe();
    }

    /** Dev helper: POST /api/alerts/test fires a fake heavy-rain alert. */
    @PostMapping("/test")
    public void test() {
        WeatherResponse.Current current = new WeatherResponse.Current(
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).toString(),
                900,    // interval (seconds)
                27.4,   // temperature
                88,     // humidity
                18.5,   // wind speed
                2.4,    // rain
                0.0,    // showers
                2.4,    // precipitation (per 15 min -> 9.6 mm/h)
                65);    // weather code: heavy rain

        rainAlertService.broadcast(
                new RainInfo(13.0827, 80.2707, true, "Heavy rain", 2.4, current));
    }
}
