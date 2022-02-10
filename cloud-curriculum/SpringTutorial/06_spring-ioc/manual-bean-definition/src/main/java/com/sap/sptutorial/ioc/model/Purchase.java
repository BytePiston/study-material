package com.sap.sptutorial.ioc.model;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Purchase {

    private Customer customer;
    private ZonedDateTime startTime;

    public Purchase(Customer customer) {
        this.customer = customer;
    }
}