package com.sap.sptutorial.restclient;

import java.io.IOException;
import java.time.Instant;

import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseExtractor;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.sap.sptutorial.restclient.model.Weather;

public class WeatherResponseExtractor
        implements ResponseExtractor<Weather> {
    @Override
    public Weather extractData(ClientHttpResponse response)
            throws IOException {
        Weather weather = new Weather();
        if (response.getHeaders().getContentType()
                .includes(MediaType.APPLICATION_JSON)) {
            DocumentContext ctxt = JsonPath
                    .parse(response.getBody());
            weather.setId(
                    ctxt.read("$.weather[0].id", Integer.class));
            weather.setName(ctxt.read("$.name", String.class));
            weather.setSunrise(Instant.ofEpochSecond(ctxt.read("$.sys.sunrise", Long.class)));
            weather.setSunset(Instant.ofEpochSecond(ctxt.read("$.sys.sunset", Long.class)));
            weather.setCountry(ctxt.read("$.sys.country", String.class));
            weather.setTemperature(ctxt.read("$.main.temp", Integer.class));
            weather.setTempMin(ctxt.read("$.main.temp_min", Integer.class));
            weather.setTempMax(ctxt.read("$.main.temp_max", Integer.class));
            weather.setPressure(ctxt.read("$.main.pressure", Integer.class));
            weather.setHumidity(ctxt.read("$.main.humidity", Integer.class));
        }
        return weather;
    }
}