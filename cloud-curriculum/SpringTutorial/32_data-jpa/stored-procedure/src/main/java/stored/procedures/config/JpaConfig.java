package stored.procedures.config;

import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import javax.sql.DataSource;
import java.sql.SQLException;

@Slf4j
@Configuration
public class JpaConfig {
  @Bean(name = "embedded", destroyMethod = "shutdown")
  public DataSource h2Embedded() {
    return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).setName("sptutor_mem").build();
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
  public JdbcTemplate jdbc(DataSource ds) {
    return new JdbcTemplate(ds);
  }
}
