package com.sap.sptutorial.jdbctx;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class JdbcTxConfig {

  @Bean(name = "h2Embedded", destroyMethod = "shutdown")
  @Profile("h2")
  public DataSource h2Embedded() {
    return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).setName("sptutor_jdbc").build();
  }

  @Bean
  public JdbcTemplate jdbcTemplate(DataSource ds) {
    return new JdbcTemplate(ds);
  }
}
