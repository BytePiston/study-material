package com.sap.icd.odata4.example.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

@Projection(name="customer_minimal", types=Customer.class)
public interface CustomerProjection {
    @Value("#{target.displayName}")
    public String getName();
}
