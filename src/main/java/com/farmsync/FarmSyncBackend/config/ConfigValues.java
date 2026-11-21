package com.farmsync.FarmSyncBackend.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties()
@Data
@NoArgsConstructor
public class ConfigValues {
    private String weatherTimezone;
    private int weatherProbabilityThreshold;
    private String weatherApiBaseUrl;
    private int weatherRegularForecaster;
    private String weatherRegularFields;
    private String weatherRegularCron;
    private double weatherMinimumPrecipitation;
    private int weatherProbabilityMinimumToRain;
    private int weatherDefaultPlusInMinute;
    private String weatherCurrentFields;
    private String globalUserId;
}
