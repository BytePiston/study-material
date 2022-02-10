package com.sap.sptutorial.authentication.saml.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.sap.sptutorial.authentication.saml.SamlController;

@SpringBootApplication
@ComponentScan(basePackageClasses = {SamlController.class})
@EnableWebMvc
public class AuthenticationSamlTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationSamlTestApplication.class, args);
	}
}
