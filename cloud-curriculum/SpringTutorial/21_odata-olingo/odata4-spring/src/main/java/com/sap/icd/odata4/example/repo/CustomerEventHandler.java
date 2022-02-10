package com.sap.icd.odata4.example.repo;

import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

import com.sap.icd.odata4.example.model.Customer;

@RepositoryEventHandler(Customer.class)
public class CustomerEventHandler {
    @HandleBeforeSave
    public void handleCustomerSave(Customer c) {
        
    }
}
