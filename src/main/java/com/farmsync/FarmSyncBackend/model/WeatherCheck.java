package com.farmsync.FarmSyncBackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherCheck {
    String longitude;
    String latitude;
}
