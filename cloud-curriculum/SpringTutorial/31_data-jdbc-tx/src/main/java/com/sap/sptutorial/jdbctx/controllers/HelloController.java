package com.sap.sptutorial.jdbctx.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {
  @Value("${greeting.title:World}")
  private String name;

  @GetMapping(path = {"/hello", "/ping"})
  @ResponseBody
  public String hello(@RequestParam("name") String who) {
    return String.format("Hello %s", who == null || who.isEmpty() ? name : who);
  }
}
