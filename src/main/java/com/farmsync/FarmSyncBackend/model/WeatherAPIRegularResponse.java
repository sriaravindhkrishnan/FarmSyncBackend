package com.farmsync.FarmSyncBackend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherAPIRegularResponse(
        double latitude,
        double longitude,
        Hourly hourly
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Hourly(
            List<String> time,
            @JsonProperty("precipitation_probability") List<Double> rainPredictionProbability,
            List<Double> rain,
            List<Double> precipitation,
            List<Double> showers,
            @JsonProperty("weather_code") List<Double> weatherCode) {
    }

}
