package com.sap.sptutorial.jarapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class BasicWebApp {

    public static void main(String[] args) {
        SpringApplication.run(BasicWebApp.class, args);
    }

    @RestController
    @RequestMapping("hello")
    class HelloController {
        @GetMapping
        public String getMessage(@RequestParam(defaultValue = "Hugo") String name) {
            return "hello " + name;
        }
    }
}
