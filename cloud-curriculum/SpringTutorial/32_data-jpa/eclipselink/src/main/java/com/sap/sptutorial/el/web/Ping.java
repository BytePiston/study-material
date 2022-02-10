package com.sap.sptutorial.el.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Ping {
  @RequestMapping("ping")
  @ResponseBody
  public String helloWorld() {
    return "Pong";
  }
}
