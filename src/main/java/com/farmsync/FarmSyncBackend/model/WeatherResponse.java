package com.farmsync.FarmSyncBackend.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherResponse(
        double latitude,
        double longitude,
        Current current) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Current(
            String time,
            int interval,                                  // 900 seconds (15 min)
            @JsonProperty("temperature_2m") double temperature,
            @JsonProperty("relative_humidity_2m") int humidity,
            @JsonProperty("wind_speed_10m") double windSpeed,
            double rain,
            double showers,
            double precipitation,
            @JsonProperty("weather_code") int weatherCode) {

        public double hourlyPrecipitationRate() {
            if (interval <= 0) return precipitation;
            return precipitation * (3600.0 / interval);
        }

        public boolean isHeavyRain(boolean isDummy) {
            return true;
        }

        public boolean isHeavyRain() {
            // 1. Check actual rainfall intensity (>= 7.5 mm/h)
            boolean heavyIntensity = hourlyPrecipitationRate() >= 7.5;

            // 2. Check WMO weather codes for heavy rain/showers or thunderstorms
            // 65: Heavy rain, 82: Violent rain showers, 95/96/99: Thunderstorm
            boolean severeWeatherCode = weatherCode == 65 || weatherCode == 82 || weatherCode >= 95;

            return heavyIntensity || severeWeatherCode;
        }
    }
}