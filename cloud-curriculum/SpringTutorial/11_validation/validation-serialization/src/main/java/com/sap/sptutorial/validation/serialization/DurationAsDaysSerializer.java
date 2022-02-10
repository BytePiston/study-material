package com.sap.sptutorial.validation.serialization;

import java.io.IOException;
import java.time.Duration;

import org.springframework.boot.jackson.JsonComponent;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

@JsonComponent
public class DurationAsDaysSerializer extends JsonSerializer<Duration>{
	@Override
	public void serialize(Duration value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {
			gen.writeNumber(value.toDays());
		
	}
}
