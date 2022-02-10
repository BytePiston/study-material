package com.sap.sptutorial.rest.deal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

@Component
public class DealOrderResourceProcessor implements ResourceProcessor<Resource<DealOrder>> {
    @Value("${customer.url}")
    private String customerUrlString;

    @Override
    public Resource<DealOrder> process(Resource<DealOrder> dealOrderResource) {
        if (customerUrlString != null) {
            Long customerId = dealOrderResource.getContent().getCustomerId();
            if (customerId != null) {
                Link customerLink = new Link(customerUrlString + "/" + customerId.toString()).withRel("customer");
                dealOrderResource.add(customerLink);
            }
        }
        return dealOrderResource;
    }
}
