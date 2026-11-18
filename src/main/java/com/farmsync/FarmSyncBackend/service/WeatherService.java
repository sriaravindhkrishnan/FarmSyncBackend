package com.farmsync.FarmSyncBackend.service;


import com.farmsync.FarmSyncBackend.model.RainInfo;
import com.farmsync.FarmSyncBackend.model.WeatherCheck;
import com.farmsync.FarmSyncBackend.model.WeatherResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherService {

    private final RestClient restClient;

    public WeatherService(@Value("${weather.api.base-url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public WeatherResponse getWeather(WeatherCheck wetherCheck) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/forecast")
                        .queryParam("latitude", wetherCheck.getLatitude())
                        .queryParam("longitude", wetherCheck.getLongitude())
                        .queryParam("current",
                                "temperature_2m,relative_humidity_2m,wind_speed_10m,rain,precipitation,weather_code")
                        .queryParam("daily", "rain_sum,precipitation_probability_max")
                        .queryParam("forecast_days", 3)
                        .queryParam("timezone", "auto")
                        .build())
                .retrieve()
                .body(WeatherResponse.class);
    }

    public RainInfo getRainInfo(WeatherCheck wetherCheck) {
        WeatherResponse w = getWeather(wetherCheck);

        List<RainInfo.DailyRain> forecast = createForecast(w);

        int code = w.current().weatherCode();
        return new RainInfo(
                w.latitude(),
                w.longitude(),
                w.current().rain() > 0 || isRainCode(code),
                describe(code),
                w.current().rain(),
                forecast, w.current());
    }

    private static @NonNull List<RainInfo.DailyRain> createForecast(WeatherResponse w) {
        List<RainInfo.DailyRain> forecast = new ArrayList<>();
        WeatherResponse.Daily daily = w.daily();
        if (daily != null) {
            for (int i = 0; i < daily.time().size(); i++) {
                forecast.add(new RainInfo.DailyRain(
                        daily.time().get(i),
                        daily.rainSum().get(i) == null ? 0.0 : daily.rainSum().get(i),
                        daily.rainProbabilityMax().get(i) == null ? 0 : daily.rainProbabilityMax().get(i)));
            }
        }
        return forecast;
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