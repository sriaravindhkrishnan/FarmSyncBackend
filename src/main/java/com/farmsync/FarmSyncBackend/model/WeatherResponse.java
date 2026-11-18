package com.farmsync.FarmSyncBackend.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherResponse(
        double latitude,
        double longitude,
        Current current,
        Daily daily) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Current(
            String time,
            @JsonProperty("temperature_2m") double temperature,
            @JsonProperty("relative_humidity_2m") int humidity,
            @JsonProperty("wind_speed_10m") double windSpeed,
            double rain,                                   // mm in the last hour
            double precipitation,                          // rain + showers + snow, mm
            @JsonProperty("weather_code") int weatherCode) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Daily(
            List<String> time,
            @JsonProperty("rain_sum") List<Double> rainSum,
            @JsonProperty("precipitation_probability_max") List<Integer> rainProbabilityMax) {
    }
}