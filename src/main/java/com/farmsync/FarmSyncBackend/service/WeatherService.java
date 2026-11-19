package com.farmsync.FarmSyncBackend.service;


import com.farmsync.FarmSyncBackend.config.ConfigValues;
import com.farmsync.FarmSyncBackend.model.RainInfo;
import com.farmsync.FarmSyncBackend.model.WeatherAPIRegularResponse;
import com.farmsync.FarmSyncBackend.model.WeatherCheck;
import com.farmsync.FarmSyncBackend.model.WeatherResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

@Service
public class WeatherService {

    private final RestClient restClient;
    private final ConfigValues configValues;
    private final TaskScheduler taskScheduler;
    private final RainAlertService rainAlertService;

    private List<LocalDateTime> scheduledSlots = List.of();
    private final List<ScheduledFuture<?>> pendingChecks = new ArrayList<>();

    public WeatherService(ConfigValues configValues, TaskScheduler taskScheduler,  RainAlertService rainAlertService) {
        this.configValues = configValues;
        this.restClient = RestClient.builder().baseUrl(configValues.getWeatherApiBaseUrl()).build();
        this.taskScheduler = taskScheduler;
        this.rainAlertService = rainAlertService;
    }

    private WeatherAPIRegularResponse getWeatherRegular(WeatherCheck wetherCheck) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/forecast")
                        .queryParam("latitude", wetherCheck.getLatitude())
                        .queryParam("longitude", wetherCheck.getLongitude())
                        .queryParam("hourly", configValues.getWeatherRegularFields())
                        .queryParam("forecast_hours", configValues.getWeatherRegularForecaster())
                        .queryParam("timezone", configValues.getWeatherTimezone())
                        .build())
                .retrieve()
                .body(WeatherAPIRegularResponse.class);
    }

    public WeatherAPIRegularResponse getWeatherRegularInfo(WeatherCheck wetherCheck) {
        WeatherAPIRegularResponse weatherAPIRegularResponseInfo = getWeatherRegular(wetherCheck);
        processRegularWeatherInfo(weatherAPIRegularResponseInfo, wetherCheck);
        return weatherAPIRegularResponseInfo;
    }
    private void cancelPendingChecks() {
        for (ScheduledFuture<?> future : pendingChecks) {
            if (!future.isDone()) {
                future.cancel(false);
            }
        }
        pendingChecks.clear();
    }


    private void processRegularWeatherInfo(WeatherAPIRegularResponse weatherAPIRegularResponseInfo, WeatherCheck wetherCheck) {
        cancelPendingChecks();
        List<LocalDateTime> slots = new ArrayList<>();
        ZoneId zone = ZoneId.of(configValues.getWeatherTimezone());
        ZonedDateTime now = ZonedDateTime.now(zone);
        if (weatherAPIRegularResponseInfo != null && weatherAPIRegularResponseInfo.hourly() != null) {
            WeatherAPIRegularResponse.Hourly hourly = weatherAPIRegularResponseInfo.hourly();
            List<String> times = hourly.time();
            List<Double> probabilities = hourly.rainPredictionProbability();
            List<Double> precipitation = hourly.precipitation();

            if (times != null && probabilities != null) {
                int size = Math.min(times.size(), Math.min(probabilities.size(), precipitation.size()));
                for (int i = 0; i < size; i++) {
                    Double prob = probabilities.get(i);
                    Double precp = precipitation.get(i);
                    if (prob == null || precp == null) {
                        continue;
                    }
                    LocalDateTime slotTime = LocalDateTime.parse(times.get(i)).plusMinutes(configValues.getWeatherDefaultPlusInMinute());
                    ZonedDateTime slotZoned = slotTime.atZone(zone);
                    boolean isHighConfidenceRain = prob > configValues.getWeatherProbabilityThreshold(); // > 80
                    boolean isHeavyShowerOverride = prob >= configValues.getWeatherProbabilityMinimumToRain() && precp >= configValues.getWeatherMinimumPrecipitation();

                    if ((isHighConfidenceRain || isHeavyShowerOverride) && slotZoned.isAfter(now)) {
                        slots.add(slotTime);
                        ScheduledFuture<?> future = updateSlotToScheduler(slotTime, slotZoned, wetherCheck);
                        pendingChecks.add(future);
                    } else if(i == 0){
                        getDummySlotToTest(now, wetherCheck, slots);
                    }
                }
            }
        }


        scheduledSlots = Collections.unmodifiableList(slots);
        System.out.println("[RAIN-SCHEDULE] Scheduled slots at " + now + ": " + scheduledSlots);

//        return Map.of(
//                "scheduledAt", now.toString(),
//                "threshold", configValues.getWeatherProbabilityThreshold(),
//                "scheduledSlots", scheduledSlots.stream().map(LocalDateTime::toString).toList()
//        );

    }

    private ScheduledFuture<?> updateSlotToScheduler(LocalDateTime slotTime, ZonedDateTime slotZoned, WeatherCheck wetherCheck) {
        return taskScheduler.schedule(
                () -> onSlotTriggered(slotTime, wetherCheck),
                slotZoned.toInstant()
        );
    }

    private void getDummySlotToTest(ZonedDateTime now, WeatherCheck wetherCheck, List<LocalDateTime> slots) {
        ZoneId zone = ZoneId.of(configValues.getWeatherTimezone());
        System.out.println(now);
        String formatted = now
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        System.out.println(formatted);
        LocalDateTime slotTime = LocalDateTime.parse(formatted.toString()).plusMinutes(1);
        ZonedDateTime slotZoned = slotTime.atZone(zone);
        ScheduledFuture<?> future = updateSlotToScheduler(slotTime, slotZoned, wetherCheck);
        System.out.println(future);
        pendingChecks.add(future);
        System.out.println(pendingChecks);
        slots.add(slotTime);
    }

    private void onSlotTriggered(LocalDateTime slotTime, WeatherCheck wetherCheck) {
        System.out.println("[RAIN-DYANAMIC-SCHEDULE] Triggered slot: " + slotTime);
        getRainInfo(wetherCheck, slotTime);
    }


    public WeatherResponse getCurrentWeather(WeatherCheck wetherCheck) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/forecast")
                        .queryParam("latitude", wetherCheck.getLatitude())
                        .queryParam("longitude", wetherCheck.getLongitude())
                        .queryParam("current", configValues.getWeatherCurrentFields())
                        .queryParam("timezone", configValues.getWeatherTimezone())
                        .build())
                .retrieve()
                .body(WeatherResponse.class);
    }
//
    public void getRainInfo(WeatherCheck wetherCheck, LocalDateTime slotTime) {
        WeatherResponse w = getCurrentWeather(wetherCheck);
        int code = w.current().weatherCode();
//        boolean isHeavyRain = w.current().isHeavyRain();
        boolean isHeavyRain = w.current().isHeavyRain(true);
        if(isHeavyRain){
            RainInfo rainInfo =  new RainInfo(
                    w.latitude(),
                    w.longitude(),
                    w.current().rain() > 0 || isRainCode(code),
                    describe(code),
                    w.current().rain(),
                    w.current());

            System.out.println("[RAIN-CONFIRMED] RainInfo: " + rainInfo);
            rainAlertService.broadcast(rainInfo);
            scheduledSlots = new ArrayList<>();
        }
    }

    private boolean isRainCode(int code) {
        return (code >= 51 && code <= 67)
                || (code >= 80 && code <= 82)
                || (code >= 95 && code <= 99);
    }

    private String describe(int code) {
        return switch (code) {
            case 0 -> "Clear sky";
            case 1, 2, 3 -> "Partly cloudy";
            case 45, 48 -> "Fog";
            case 51, 53, 55 -> "Drizzle";
            case 56, 57 -> "Freezing drizzle";
            case 61 -> "Light rain";
            case 63 -> "Moderate rain";
            case 65 -> "Heavy rain";
            case 66, 67 -> "Freezing rain";
            case 71, 73, 75, 77 -> "Snow";
            case 80, 81, 82 -> "Rain showers";
            case 85, 86 -> "Snow showers";
            case 95 -> "Thunderstorm";
            case 96, 99 -> "Thunderstorm with hail";
            default -> "Unknown";
        };
    }
}