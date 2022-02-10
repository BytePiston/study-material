package com.sap.sptutorial.rest.deal;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class Hello {
	private String hello = "Hello World!";
	
	public String getGreeting() {
		return getHello();
	}
}
