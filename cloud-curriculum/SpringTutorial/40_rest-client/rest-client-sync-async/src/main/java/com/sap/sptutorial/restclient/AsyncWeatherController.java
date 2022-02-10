package com.sap.sptutorial.restclient;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.sap.sptutorial.restclient.model.Weather;

import lombok.extern.log4j.Log4j;

@RestController
@RequestMapping("weather/async")
@Log4j
public class AsyncWeatherController {

    private AsyncWeatherService service;

    @Autowired
    public AsyncWeatherController(AsyncWeatherService service) {
        this.service = service;
    }

    @GetMapping
    public DeferredResult<List<Weather>> getWeatherForLocations() {
        DeferredResult<List<Weather>> response = new DeferredResult<>();
        service.readWeatherForLocations()
                .addCallback(new ListenableFutureCallback<List<Weather>>() {

                    @Override
                    public void onSuccess(List<Weather> weatherList) {
                        response.setResult(weatherList);
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        response.setErrorResult(ex);
                    }
                });
        return response;
    }

    @GetMapping("now/{country}/{city}")
    public DeferredResult<Weather> getWeatherForLocation(
            @PathVariable("country") String country,
            @PathVariable("city") String city, @RequestHeader("tenant") String tenant) {
        RestClientConfiguration.tenantId.set(tenant);
        DeferredResult<Weather> response = new DeferredResult<>();
        service.readWeather(country, city)
                .addCallback(new ListenableFutureCallback<Weather>() {

                    @Override
                    public void onSuccess(Weather weather) {
                        log.info("response tenant: " + RestClientConfiguration.tenantId.get());
                        response.setResult(weather);
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        response.setErrorResult(ex);
                    }
                });
        return response;
    }
}
