package com.sap.sptutorial.ioc;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;

import com.sap.sptutorial.ioc.model.Customer;
import com.sap.sptutorial.ioc.model.Mailing;
import com.sap.sptutorial.ioc.model.Purchase;

public class SpringConfig {
    @Bean
    @Scope(scopeName = "prototype")
    public Purchase purchase(Customer customer1) {
        return new Purchase(customer1);
    }

    @Bean
    @Scope(scopeName = "prototype")
    public Purchase purchase2(@Qualifier("customer2") Customer customer) {
        return new Purchase(customer);
    }

    @Bean
    public Customer customer1() {
        Customer customer = new Customer();
        customer.setName("Gregor");
        return customer;
    }

    @Bean
    public Customer customer2() {
        Customer customer = new Customer();
        customer.setName("Andreas");
        return customer;
    }

    @Profile("prod")
    @Bean("customer3")
    public Customer customerTest() {
        Customer customer = new Customer();
        customer.setName("Tester");
        return customer;
    }
    
    @Profile("test")
    @Bean("customer3")
    public Customer customerProd() {
        Customer customer = new Customer();
        customer.setName("Stefan");
        return customer;
    }

    @Bean
    public Mailing mailing(List<Customer> customers) {
        return new Mailing(customers);
    }
}
