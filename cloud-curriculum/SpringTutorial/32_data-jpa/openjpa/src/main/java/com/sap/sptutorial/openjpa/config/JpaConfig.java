package com.sap.sptutorial.openjpa.config;

import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.OpenJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.SQLException;

@Slf4j
@Configuration
@EnableTransactionManagement
public class JpaConfig {
  @Bean(name = "embedded", destroyMethod = "shutdown")
  @Profile("default")
  public DataSource h2Embedded() {
    return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).setName("sptutor_jpa").build();
  }

  @Bean(name = "standalone")
  @DependsOn("h2TcpServer")
  @Profile("dev")
  public DataSource h2Standalone() {
    DriverManagerDataSource ds = new DriverManagerDataSource();
    ds.setDriverClassName("org.h2.Driver");
    ds.setUrl("jdbc:h2:tcp://localhost:9092/~/sptutor_jpa");
    ds.setUsername("sa");
    ds.setPassword("");
    return ds;
  }

  @Bean(name = "h2TcpServer", destroyMethod = "stop")
  @Profile("dev")
  public Server h2TcpServer() throws SQLException {
    return Server.createTcpServer("-tcpPort", "9092", "-trace").start();
  }

  @Bean
  public JpaVendorAdapter openJpa() {
    OpenJpaVendorAdapter adapter = new OpenJpaVendorAdapter();
    //adapter.setDatabase(Database.H2);
    adapter.setShowSql(true);
    adapter.setGenerateDdl(true);
    return adapter;
  }

  @Bean(name = "entityManagerFactory")
  public LocalContainerEntityManagerFactoryBean emf(JpaVendorAdapter adapter, DataSource ds) {
    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setPackagesToScan("com.sap.sptutorial.openjpa.domain");
    factory.setJpaVendorAdapter(adapter);
    factory.setDataSource(ds);
    //factory.setJtaDataSource(ds);
    return factory;
  }

  @Bean
  public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
    return new JpaTransactionManager(emf);
  }
}
