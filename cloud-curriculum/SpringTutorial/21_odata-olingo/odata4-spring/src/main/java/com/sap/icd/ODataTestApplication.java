package com.sap.icd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.sap.icd.odata4.repo.ODataRepositoryExtensionImpl;

@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = ODataRepositoryExtensionImpl.class)
public class ODataTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(ODataTestApplication.class, args);
	}
}
