package com.sap.sptutorial.hibernate;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import javax.sql.DataSource;

import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@Configuration
public class JpaConfig {

	@Profile("default")
	@Bean
	public DataSource h2Embedded() {
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).setName("testdb").build();
	}

	@Profile("pool")
	@Bean(name="ds1")
	public DataSource tomcatPool() {
		org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();
		ds.setDriverClassName("org.h2.Driver");
		ds.setUrl("jdbc:h2:mem:testdb");
		ds.setUsername("sa");
		ds.setPassword("");
		return ds;
	}

	@DependsOn("h2TcpServer")
	@Profile("dev")
	@Bean
	public DataSource h2Standalone() {
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setDriverClassName("org.h2.Driver");
		ds.setUrl("jdbc:h2:tcp://localhost:9095/~/testdb");
		ds.setUsername("sa");
		ds.setPassword("");
		return ds;
	}

	@Bean(name = "h2TcpServer", destroyMethod = "stop")
	@Profile("dev")
	public Server h2TcpServer() throws SQLException {
		return Server.createTcpServer("-tcpPort", "9095", "-trace").start();
	}
}
