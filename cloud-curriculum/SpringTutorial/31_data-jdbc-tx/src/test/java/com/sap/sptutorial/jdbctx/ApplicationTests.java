package com.sap.sptutorial.jdbctx;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@DirtiesContext
public class ApplicationTests {

  @Value("${local.server.port}")
  private int port;
  private String url;

  @Before
  public void init() {
    url = String.format("http://localhost:%s/", port);
  }

  @Test
  public void testPing() throws Exception {
    ResponseEntity<String> entity = new TestRestTemplate(null).getForEntity(
            url + "ping?name=", String.class);
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertTrue(entity.getBody().matches("^Hello\\sW[a-zA-Z]+$"));
  }

  @Test
  public void testHello() {
    ResponseEntity<String> entity = new TestRestTemplate(null).getForEntity(url + "hello?name=test", String.class);
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertTrue(entity.getBody().endsWith("test"));
  }
}
