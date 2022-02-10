package com.sap.sptutorial.unittesting.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class SimpleWebController {
  @RequestMapping(value = "/ping", method = RequestMethod.GET)
  @ResponseBody
  public String ping() {
    return "Pong";
  }

  @RequestMapping(value = "/ping", method = RequestMethod.POST)
  @ResponseBody
  public String ping(@Param("echo") String echo) {
    log.debug(">>>>>>{}", echo);
    return echo;
  }
}
