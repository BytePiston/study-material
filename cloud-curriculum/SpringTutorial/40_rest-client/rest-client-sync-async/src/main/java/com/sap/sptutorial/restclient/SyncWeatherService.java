package com.sap.sptutorial.restclient;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sap.sptutorial.restclient.model.Weather;

@Service
public class SyncWeatherService {

    private ConnectionSettings settings;
    private RestTemplate restTemplate;

    @Autowired
    public SyncWeatherService(ConnectionSettings settings,
            RestTemplate restTemplate) {
        this.settings = settings;
        this.restTemplate = restTemplate;
    }

    public Weather readWeather(String country, String city) {
        return restTemplate.execute(settings.getWeatherUrl(), HttpMethod.GET,
                new JsonRequestCallback(), new WeatherResponseExtractor(), city,
                country, settings.getApiCode());
    }

    public List<Weather> readWeatherForLocations() {
        return settings.getLocations().parallelStream().map(location -> {
            return readWeather(location.getCity(), location.getCountry());
        }).collect(Collectors.toList());
    }
}
