package com.sap.sptutorial.validation.controller;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.sap.sptutorial.validation.model.Deal;
import com.sap.sptutorial.validation.service.DealService;

@RestController
@RequestMapping("deal")
public class DealController {

    @Autowired
    private DealService service;

    @PostMapping
    public ResponseEntity<?> createDeal(@RequestBody Deal deal) {
        service.put(deal);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/" + deal.getId()).build().toUri());
        return new ResponseEntity<>(null, headers, HttpStatus.CREATED);
    }

    @GetMapping("all")
    public Collection<Deal> getAllDeals() {
        return service.getAll();
    }

    @GetMapping("time")
    public Collection<Deal> getAllDealsFromTo(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date to) {
        return service.getAllFromTo(from, to);
    }
}
