package com.sap.sptutorial.unittesting.config;

import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.SQLException;

@Slf4j
@Configuration
public class JpaConfig {
  @Bean(name = "embedded", destroyMethod = "shutdown")
  @Profile("default")
  public DataSource h2Embedded() {
    return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).setName("sptutor_jpa").build();
  }

  @Bean(name = "testDS", destroyMethod = "shutdown")
  @Profile("qa")
  public DataSource testDatasource() {
    return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).setName("sptutor_test").build();
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
  public JpaVendorAdapter eclipseLink() {
    EclipseLinkJpaVendorAdapter adapter = new EclipseLinkJpaVendorAdapter();
    //adapter.setDatabase(Database.H2);
    adapter.setShowSql(true);
    adapter.setGenerateDdl(true);
    return adapter;
  }

  @Bean(name = "entityManagerFactory")
  public LocalContainerEntityManagerFactoryBean emf(JpaVendorAdapter adapter, DataSource ds) {
    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setPackagesToScan("com.sap.sptutorial.unittesting.domain");
    factory.setJpaVendorAdapter(adapter);
    factory.setJtaDataSource(ds);
    return factory;
  }

  @Bean
  public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
    return new JpaTransactionManager(emf);
  }

  @Bean
  public RepositoryRestConfigurer repositoryRestConfigurer() {
    return new RepositoryRestConfigurerAdapter() {
      @Override
      public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.setBasePath("/rest");
      }
    };
  }
}