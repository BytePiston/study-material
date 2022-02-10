package com.sap.sptutorial.restclient;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sap.sptutorial.restclient.model.Weather;

@RestController
@RequestMapping("weather/sync")
public class SyncWeatherController {

    private SyncWeatherService service;

    @Autowired
    public SyncWeatherController(SyncWeatherService service) {
        this.service = service;
    }

    @GetMapping
    public List<Weather> getWeatherForLocations() {
        return service.readWeatherForLocations();
    }

    @GetMapping("now/{country}/{city}")
    public Weather getWeatherForLocation(
            @PathVariable("country") String country,
            @PathVariable("city") String city) {
        return service.readWeather(country, city);
    }
}
