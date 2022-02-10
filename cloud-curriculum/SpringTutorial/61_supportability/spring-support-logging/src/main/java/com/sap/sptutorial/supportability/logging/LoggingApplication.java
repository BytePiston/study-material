package com.sap.sptutorial.supportability.logging;

import javax.naming.NamingException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LoggingApplication {
    public static void main(String[] args) throws NamingException {
        SpringApplication.run(LoggingApplication.class, args);
    }
}
