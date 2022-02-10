package com.sap.icd.odata4.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.olingo.server.api.ODataHttpHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sap.icd.odata4.processor.HandlerFactory;

@RestController
@RequestMapping("${odata.servicePath}")
public class ODataController {
    private HandlerFactory handlerFactory;
    
    public ODataController(HandlerFactory handlerFactory) {
        this.handlerFactory = handlerFactory;
    }

    @RequestMapping("**")
    public void handle(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        ODataHttpHandler handler = handlerFactory.getHandlerInstance();
        handler.process(req, resp);
    }
}