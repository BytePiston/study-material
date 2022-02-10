package com.sap.sptutorial.supportability.logging;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import org.slf4j.MDC;
import org.slf4j.MarkerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("deal")
@Slf4j
public class DealController {

    private static String DEALEX = "DEAL EXCEPTION";
    private static String ID = "DEAL ID";
    private HashMap<String, Deal> deals = new HashMap<>();

    public DealController() {
        if (log.isTraceEnabled()) {
            log.trace("Initialize constructor "
                    + this.getClass().getConstructors()[0].getName());
        }
        Deal example = new Deal();
        example.setId("DE_5631");
        example.setDealPriceAmount(56.3);
        example.setRedeemableFrom(new Date());
        MDC.put(ID, example.getId());
        deals.put(example.getId(), example);
        log.info("Deal created");
    }

    @GetMapping
    public Collection<Deal> getAllDeals() {
        log.trace("Return all deals");
        return deals.values();
    }

    @GetMapping(value = "{id}")
    public Deal getDealById(@PathVariable("id") String id) {
        MDC.put(ID, id);
        log.trace("Return deal");
        Deal deal = deals.get(id);
        if (deal == null) {
            log.error(MarkerFactory.getMarker(DEALEX), "Deal not found");
            throw new IllegalArgumentException();
        }
        return deal;
    }

    @PostMapping
    public Deal createDeal(@RequestBody Deal newdeal) {
        if (newdeal == null) {
            log.error(MarkerFactory.getMarker(DEALEX), "Deal must not be null");
            throw new IllegalArgumentException();
        }
        if (newdeal.getId() == null) {
            log.error(MarkerFactory.getMarker(DEALEX),
                    "Deal id must not be null");
            throw new IllegalArgumentException();
        }
        MDC.put(ID, newdeal.getId());
        deals.put(newdeal.getId(), newdeal);
        log.trace("Deal created");
        return newdeal;
    }
}
