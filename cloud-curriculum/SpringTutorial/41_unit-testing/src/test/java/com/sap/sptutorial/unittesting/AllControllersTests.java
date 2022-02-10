package com.sap.sptutorial.unittesting;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JpaApplication.class)
@WebAppConfiguration
@DirtiesContext
public class AllControllersTests {
  private MockMvc mock;

  @Autowired
  private WebApplicationContext web;

  @Before
  public void createMock() {
    mock = MockMvcBuilders.webAppContextSetup(web).build();
  }

  @Test
  public void getPing() throws Exception {
    mock.perform(get("/ping")).andExpect(status().isOk()).andExpect(content().string("Pong"));
  }

  @Test
  public void postPing() throws Exception {
    String echo = "echo";
    String hello = "hello";
    mock.perform(post("/ping").contentType(MediaType.APPLICATION_FORM_URLENCODED).param(echo, hello))
            .andExpect(status().isOk()).andExpect(content().string(hello));
  }

  @Test
  public void requestRestControllerShouldBeOk() throws Exception {
    mock.perform(get("/api/employee/1")).andExpect(status().isOk())
            .andDo(result -> log.debug(">>>>>>Result status={}", result.getResponse().getStatus()));
  }
}
