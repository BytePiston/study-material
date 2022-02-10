package com.sap.sptutorial.restclient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;
import org.springframework.web.client.AsyncRestTemplate;

import com.sap.sptutorial.restclient.model.Weather;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AsyncWeatherService {

    private ConnectionSettings settings;
    private AsyncRestTemplate restTemplate;
    private ExecutorService executor;

    @Autowired
    public AsyncWeatherService(ConnectionSettings settings,
            AsyncRestTemplate restTemplate) {
        this.settings = settings;
        this.restTemplate = restTemplate;
        executor = Executors.newFixedThreadPool(2);
    }

    public ListenableFuture<Weather> readWeather(String country, String city) {
        return restTemplate.execute(settings.getWeatherUrl(), HttpMethod.GET,
                new JsonRequestCallback(), new WeatherResponseExtractor(), city,
                country, settings.getApiCode());
    }

    public ListenableFuture<List<Weather>> readWeatherForLocations() {
        ListenableFutureTask<List<Weather>> futureWeatherList = new ListenableFutureTask<>(
                () -> {
                    return settings.getLocations().stream().map(location -> {
                        return readWeather(location.getCity(),
                                location.getCountry());
                    }).collect(ArrayList::new, (list, element) -> {
                        try {
                            list.add(element.get());
                        } catch (InterruptedException | ExecutionException e) {
                            log.error("Problems reading weather data", e);
                        }
                    }, ArrayList::addAll);
                });
        executor.execute(futureWeatherList);
        return futureWeatherList;
    }
}
