package com.sap.sptutorial.jdbctx;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@PropertySource("classpath:application.properties")
public class Application {

  @Value("classpath:init.sql")
  private Resource initSqlScript;

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public CommandLineRunner init(DataSource ds) {
    return args -> {
      log.debug("Initializing ...");
      ScriptUtils.executeSqlScript(ds.getConnection(), initSqlScript);
    };
  }
}
