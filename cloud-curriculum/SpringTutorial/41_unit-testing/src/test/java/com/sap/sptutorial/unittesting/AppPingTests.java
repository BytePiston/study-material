package com.sap.sptutorial.unittesting;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JpaApplication.class)
//@WebAppConfiguration
//@WebIntegrationTest("server.port:0")
@WebIntegrationTest(randomPort = true)
@DirtiesContext
@ActiveProfiles("qa")
public class AppPingTests {

  @Value("${local.server.port}")
  private int port;
  private String url;

  @Before
  public void init() {
    log.debug("port: {}", port);
    url = String.format("http://localhost:%s/ping", port);
  }

  @Test
  public void pingTest() {
    ResponseEntity<String> resp = new TestRestTemplate(null).getForEntity(url, String.class);
    log.info(resp.toString());
    assertEquals(HttpStatus.OK, resp.getStatusCode());
  }
}
