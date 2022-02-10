package stored.procedures;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@SpringBootApplication
public class JpaApplication {

  public static void main(String[] args) throws Exception {
    SpringApplication.run(JpaApplication.class, args);
  }

  @Bean
  CommandLineRunner random() {
    return new CommandLineRunner() {
      @Autowired
      private JdbcTemplate jdbc;

      @Override
      public void run(String... args) throws Exception {
        log.debug(">>>>>> Random(): {} <<<<<<", jdbc.queryForObject("CALL JAVA_RANDOM()", Double.TYPE));
        log.debug(">>>>>> Power(2, 4): {} <<<<<<", jdbc.queryForObject("CALL JAVA_POWER(2, 4)", Double.TYPE));
      }
    };
  }
}
