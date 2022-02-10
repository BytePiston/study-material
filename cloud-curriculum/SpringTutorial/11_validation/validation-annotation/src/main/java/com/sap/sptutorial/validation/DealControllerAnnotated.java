package com.sap.sptutorial.validation;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("deal_annotated")
public class DealControllerAnnotated {

    @Autowired
    private DealList deals;

    @PostMapping
    public ResponseEntity<?> createDeal(@RequestBody @Validated Deal deal) {
        deals.put(deal);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/" + deal.getId()).build().toUri());
        return new ResponseEntity<>(deal, headers, HttpStatus.CREATED);
    }

    @GetMapping
    public Collection<Deal> getAllDeals() {
        return deals.getAll();
    }
}
