package com.sap.sptutorial.ioc.model;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Component
@Scope(scopeName="purchase")
public class Customer {
    private String name;
}