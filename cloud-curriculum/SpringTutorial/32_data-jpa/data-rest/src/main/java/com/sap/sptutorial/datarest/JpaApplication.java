package com.sap.sptutorial.datarest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import javax.sql.DataSource;

@Slf4j
@SpringBootApplication
public class JpaApplication {

  @SuppressWarnings("unused")
  @Value("classpath:import.sql")
  private Resource importSqlScript;

  public static void main(String[] args) throws Exception {
    SpringApplication.run(JpaApplication.class, args);
  }

  @SuppressWarnings("unused")
  @Bean
  CommandLineRunner init(DataSource ds) {
    return args -> {
      ScriptUtils.executeSqlScript(ds.getConnection(), importSqlScript);
    };
  }
}
