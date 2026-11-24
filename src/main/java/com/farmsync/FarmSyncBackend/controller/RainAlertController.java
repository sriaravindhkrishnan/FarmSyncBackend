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
}
