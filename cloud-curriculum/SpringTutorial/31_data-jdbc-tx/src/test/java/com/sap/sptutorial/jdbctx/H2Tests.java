package com.sap.sptutorial.jdbctx;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JdbcTxConfig.class)
@ActiveProfiles("h2")
public class H2Tests {
  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationTests.class);

  @Autowired
  private DataSource h2;

  @Test
  public void checkH2DataSource() throws SQLException {
    assertNotNull(h2);
  }

  @Test
  public void accessH2DB() {
    JdbcTemplate jdbc = new JdbcTemplate(h2);
    jdbc.execute("CREATE TABLE PLAYERS (ID INTEGER PRIMARY KEY, NAME VARCHAR(256) NOT NULL, BIRTH_DATE TIMESTAMP)");
    jdbc.execute("INSERT INTO PLAYERS VALUES(1001, 'Fury Tycoon', '2001-03-04')");
    Map map = jdbc.queryForMap("SELECT * FROM PLAYERS LIMIT 1");
    assertEquals(Timestamp.valueOf("2001-03-04 00:00:00"), map.get("BIRTH_DATE"));
    jdbc.execute("DROP TABLE PLAYERS");
  }
}
