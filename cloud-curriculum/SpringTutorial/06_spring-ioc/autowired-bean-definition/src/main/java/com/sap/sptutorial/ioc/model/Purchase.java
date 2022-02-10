package com.sap.sptutorial.ioc.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Purchase {

    private Customer customer;

    @Autowired
    private Product product;

    private Channel channel;

    @Autowired
    public Purchase(Customer customer) {
        this.customer = customer;
    }

    @Autowired
    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
