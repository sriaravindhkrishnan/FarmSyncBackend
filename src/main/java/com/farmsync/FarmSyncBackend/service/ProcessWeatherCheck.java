package com.farmsync.FarmSyncBackend.service;

import com.farmsync.FarmSyncBackend.config.ConfigValues;
import com.farmsync.FarmSyncBackend.model.*;
import com.farmsync.FarmSyncBackend.repository.RainSlotRepository;
import com.farmsync.FarmSyncBackend.service.messaging.RabbitMqProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

@Service
public class ProcessWeatherCheck implements  MessageListenerFactory {

    private final RabbitMqProducer rabbitMqProducer;
    private final ObjectMapper objectMapper;
    private final ConfigValues configValues;
    private  final FarmService farmService;
    private final RestClient restClient;
    private final RainSlotRepository  rainSlotRepository;

    public ProcessWeatherCheck(RabbitMqProducer rabbitMqProducer, ObjectMapper objectMapper,
                               ConfigValues configValues,  FarmService farmService, RainSlotRepository rainSlotRepository) {
        this.rabbitMqProducer = rabbitMqProducer;
        this.objectMapper = objectMapper;
        this.configValues = configValues;
        this.farmService = farmService;
        this.restClient = RestClient.builder().baseUrl(configValues.getWeatherApiBaseUrl()).build();;
        this.rainSlotRepository = rainSlotRepository;
    }

    @Override
    public String getType() {
        return "weatherCheck";
    }

    @Override
    public void process(RabbitMQConsumedMessage payload) {
        System.out.println("Processing Weather Check");
        List<FarmDetails> farmDetails = getFarmsList();
        farmDetails.forEach(this::getWeatherRegularInfo);


//        WeatherCheck weatherCheck = new WeatherCheck();
//        weatherCheck.setLatitude("12.2222");
//        weatherCheck.setLongitude("22.2222");
//        RabbitMQConsumedMessage message = new RabbitMQConsumedMessage();
//        message.setRequestType("test");
//        message.setMessage(weatherCheck.toString());
//        try {
//            String jsonPayload = objectMapper.writeValueAsString(message);
//            rabbitMqProducer.scheduleWeatherCheckWithDelay(jsonPayload, 3600000);
//        } catch (JsonProcessingException e) {
//            throw new IllegalStateException("Failed to serialize RabbitMQConsumedMessage to JSON", e);
//        }

    }


    private List<FarmDetails>  getFarmsList(){
        String userID = configValues.getGlobalUserId();
        return farmService.getFarms(userID);
    }

    private WeatherAPIRegularResponse getWeatherRegularInfo(FarmDetails farmDetails) {
        WeatherAPIRegularResponse weatherAPIRegularResponseInfo = getWeatherRegular(farmDetails);
        System.out.println("WeatherAPIRegularResponseInfo: " + weatherAPIRegularResponseInfo.toString());
        processRegularWeatherInfo(weatherAPIRegularResponseInfo, farmDetails);
        return weatherAPIRegularResponseInfo;
    }

    private void processRegularWeatherInfo(WeatherAPIRegularResponse weatherAPIRegularResponseInfo, FarmDetails farmDetails) {
        List<RainForecastSlot> slotDetails =  new ArrayList<>();
        List<LocalDateTime> slots = new ArrayList<>();
        ZoneId zone = ZoneId.of(configValues.getWeatherTimezone());
        LocalDateTime current = LocalDateTime.now(zone);
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
//                    String time = times.get(i).replace("T", " ");
                    LocalDateTime slotTime = LocalDateTime.parse(times.get(i));
                    boolean isHighConfidenceRain = prob >= configValues.getWeatherProbabilityThreshold(); // > 80
                    boolean isHeavyShowerOverride = prob >= configValues.getWeatherProbabilityMinimumToRain() && precp >= configValues.getWeatherMinimumPrecipitation();
                    if ((isHighConfidenceRain || isHeavyShowerOverride) && slotTime.isAfter(current)) {
                        slots.add(slotTime);
                        slotDetails.add(new RainForecastSlot(
                                UUID.randomUUID().toString(),
                                farmDetails.farmId(),
                                current.toLocalDate(),
                                slotTime,
                                prob,
                                "waiting",
                                current.toString(),
                                current.toString()
                        ));

                    }
                }
            }
        }

        System.out.println("[RAIN-SCHEDULE] Scheduled slots at " + current + ": " + slots.toString());
        boolean isSlotsCreated = rainSlotRepository.saveRows(slotDetails);
        if (isSlotsCreated) {
            System.out.println("Slots Created");
        }
    }

    private WeatherAPIRegularResponse getWeatherRegular(FarmDetails farmDetails) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/forecast")
                        .queryParam("latitude", farmDetails.latitude())
                        .queryParam("longitude", farmDetails.longitude())
                        .queryParam("hourly", configValues.getWeatherRegularFields())
                        .queryParam("forecast_hours", configValues.getWeatherRegularForecaster())
                        .queryParam("timezone", configValues.getWeatherTimezone())
                        .build())
                .retrieve()
                .body(WeatherAPIRegularResponse.class);
    }
}
