package com.sap.sptutorial.ioc.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Mailing {
    private List<Customer> customers;

    public Mailing(List<Customer> customers) {
        this.customers = customers;
    }
}
