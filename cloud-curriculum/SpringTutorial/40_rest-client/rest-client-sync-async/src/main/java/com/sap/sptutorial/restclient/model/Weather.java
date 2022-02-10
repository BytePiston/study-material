package com.sap.sptutorial.restclient.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Weather {
    private Integer id;
    private String name;
    private String country;
    private Instant sunrise;
    private Instant sunset;
    private Integer temperature;
    private Integer pressure;
    private Integer humidity;
    private Integer tempMin;
    private Integer tempMax;
    
    public String getFahrenheitTemperature() {
        double fahrenheitTemp = (temperature * 1.8) - 459.67;
        return String.format("%4.2f", fahrenheitTemp);
    }

    public String getCelsiusTemperature() {
        double celsiusTemp = temperature - 273.15;
        return String.format("%4.2f", celsiusTemp);
    }
}
