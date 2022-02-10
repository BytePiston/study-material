package com.sap.sptutorial.restclient;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "connection")
@Data
public class ConnectionSettings {
	@NotEmpty
	private String weatherUrl;
	@NotEmpty
	private String forecastUrl;
	@NotEmpty
	private String apiCode;
	private List<Location> locations;
	private Proxy proxy;
	
	@Data
	public static class Proxy {
	    private String host;
	    private int port;
	}
	
	@Data
	public static class Location {
		private String country;
		private String city;
	}
}