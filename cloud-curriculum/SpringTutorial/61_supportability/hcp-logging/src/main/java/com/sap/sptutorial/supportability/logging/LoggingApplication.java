package com.sap.sptutorial.supportability.logging;

import javax.servlet.Filter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.sap.hcp.cf.logging.servlet.filter.RequestLoggingFilter;

@SpringBootApplication
public class LoggingApplication {
	public static void main(String[] args) {
		SpringApplication.run(LoggingApplication.class, args);
	}
	
	@Bean
	public Filter shallowEtagHeaderFilter() {
		return new RequestLoggingFilter();
	}
}
