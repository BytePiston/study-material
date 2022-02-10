package com.sap.sptutorial.supportability.metrics;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sap.sptutorial.supportability.metrics.TimerMetrics.Timer;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("deal")
@Slf4j
public class DealController implements HealthIndicator {
    private static String DEALS_GAUGE = "controller.deal.size";
    private static String DEALS_COUNTER = "controller.deal.created";
    private ConcurrentHashMap<String, Deal> deals = new ConcurrentHashMap<>();

    @Autowired
    private CounterService counter;
    @Autowired
    private GaugeService gauge;
    @Autowired
    private TimerMetrics timerMetrics;

    public DealController() {
        log.trace("Initialize list of deals");
        Deal example = new Deal();
        example.setId("DE_5631");
        example.setTitle("Green Candy Bar");
        example.setDealPriceAmount(56.3);
        example.setRedeemableFrom(new Date());
        deals.put(example.getId(), example);
        log.trace("Deal created");
    }

    @PostConstruct
    public void init() {
        gauge.submit(DEALS_GAUGE, deals.size());
    }

    @GetMapping
    public Collection<Deal> getAllDeals() {
        Timer timer = timerMetrics.start("getAllDeals");
        log.trace("Return all deals");
        Collection<Deal> dealValues = deals.values();
        timer.stop();
        return dealValues;
    }

    @GetMapping(value = "{id}")
    public Deal getDealById(@PathVariable("id") String id) {
        log.trace("Return deal");
        Deal deal = deals.get(id);
        if (deal == null) {
            log.error("Deal not found");
            throw new IllegalArgumentException();
        }
        return deal;
    }

    @PostMapping
    public Deal createDeal(@RequestBody Deal newdeal) {
        if (newdeal == null) {
            log.error("Deal must not be null");
            throw new IllegalArgumentException();
        }
        if (newdeal.getId() == null) {
            log.error("Deal id must not be null");
            throw new IllegalArgumentException();
        }
        deals.put(newdeal.getId(), newdeal);
        log.trace("Deal created");
        gauge.submit(DEALS_GAUGE, deals.size());
        counter.increment(DEALS_COUNTER);
        return newdeal;
    }

    @Override
    public Health health() {
        if (deals.size() < 2) {
            return Health.up().withDetail("max deals size", deals.size()).build();
        }
        return Health.down().withDetail("max deals size", deals.size()).build();
    }
}
