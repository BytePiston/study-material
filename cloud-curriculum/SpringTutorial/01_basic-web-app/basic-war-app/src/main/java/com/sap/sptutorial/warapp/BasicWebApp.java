package com.sap.sptutorial.warapp;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class BasicWebApp extends SpringBootServletInitializer {
    @RestController
    @RequestMapping("hello")
    class HelloController {
        @GetMapping
        public String getMessage(@RequestParam(defaultValue = "Hugo") String name) {
            return "hello " + name;
        }
    }
}
