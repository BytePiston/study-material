package com.sap.sptutorial.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("deal")
public class DealController {
    private final DealRepository dealRepository;

    @Autowired
    public DealController(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    @GetMapping
    public DealCollection getAllDeals() {
        return dealRepository.findAll(0, Integer.MAX_VALUE);
    }

    @GetMapping(path = "ping")
    public Deal ping(@RequestParam(name = "title", defaultValue = "ping") String title) {
        Deal deal = Deal.builder().title(title).id("ping").build();
        return deal;
    }

    @GetMapping(path = "{id}", produces = MediaType.APPLICATION_XML_VALUE)
    public Deal getDealById(@PathVariable("id") String id) {
        return dealRepository.find(id);
    }

    @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Deal getDealByIdForJson(@PathVariable("id") String id) {
        return dealRepository.find(id);
    }

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Deal> createDeal(@RequestBody Deal newdeal) {
        newdeal.setVersion(0l);
        dealRepository.add(newdeal);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(
            ServletUriComponentsBuilder.fromCurrentRequest().path("/" + newdeal.getId()).build().toUri());
        ResponseEntity<Deal> response = new ResponseEntity<>(newdeal, headers, HttpStatus.CREATED);
        return response;
    }

    @RequestMapping(path = "{id}", method = RequestMethod.HEAD)
    public ResponseEntity<?> existsDeal(@PathVariable("id") String id) {
        ResponseEntity<?> response = null;
        Long version = dealRepository.getVersion(id);
        if (version != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setETag(String.format("\"%d\"", version.longValue()));
            headers.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/" + id).build().toUri());
            response = new ResponseEntity<>(null, headers, HttpStatus.NO_CONTENT);
        } else {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return response;
    }

    @PatchMapping(path = "{id}")
    public ResponseEntity<DealPatch> updateDeal(@RequestBody DealPatch dealPatch, @PathVariable("id") String id) {
        ResponseEntity<DealPatch> response = null;
        if (dealRepository.exists(id)) {
            if (dealRepository.update(id, dealPatch) != null) {
                response = new ResponseEntity<>(dealPatch, HttpStatus.OK);
            } else {
                response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return response;
    }
}
