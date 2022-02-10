package com.sap.icd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sap.icd.odata4.example.model.Customer;
import com.sap.icd.odata4.example.model.CustomerProjection;

@RestController
@RequestMapping("pro")
public class ProjectionTest {
    @Autowired
    private ProjectionFactory factory;

    @GetMapping
    public String test() {
        Customer customer = new Customer();
        customer.setDisplayName("Hannes");
        customer.setId(123L);
        CustomerProjection projection = factory.createProjection(CustomerProjection.class, customer);
        return projection.getName();
    }
}
