package com.sap.sptutorial.validation.serialization;

import java.io.IOException;
import java.time.Duration;

import org.springframework.boot.jackson.JsonComponent;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

@JsonComponent
public class DurationAsDaysDeserializer extends JsonDeserializer<Duration>{
	@Override
	public Duration deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		long days = p.getLongValue();
		return Duration.ofDays(days);
	}

}
