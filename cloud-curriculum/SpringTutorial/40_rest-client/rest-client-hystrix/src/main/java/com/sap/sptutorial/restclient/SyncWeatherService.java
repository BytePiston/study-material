package com.sap.sptutorial.restclient;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import com.sap.sptutorial.restclient.model.Weather;

@Service
public class SyncWeatherService {

    private ConnectionSettings settings;
    private RestTemplate restTemplate;

    private final Weather fallbackWeather = Weather.builder().name("ERROR")
            .build();

    @Autowired
    public SyncWeatherService(ConnectionSettings settings,
            RestTemplate restTemplate) {
        this.settings = settings;
        this.restTemplate = restTemplate;
    }

    public Weather fallbackSingle(String country, String city) {
        return fallbackWeather;
    }

    @CacheResult
    @HystrixCommand(fallbackMethod = "fallbackSingle")
    public Weather readWeather(String country, String city) {
        return restTemplate.execute(settings.getWeatherUrl(), HttpMethod.GET,
                new JsonRequestCallback(), new WeatherResponseExtractor(), city,
                country, settings.getApiCode());
    }
    
    public List<Weather> fallbackAll() {
        return Collections.singletonList(fallbackWeather);
    }

    @CacheResult
    @HystrixCommand(fallbackMethod = "fallbackAll")
    public List<Weather> readWeatherForLocations() {
        return settings.getLocations().parallelStream().map(location -> {
            return readWeather(location.getCity(), location.getCountry());
        }).collect(Collectors.toList());
    }
}
