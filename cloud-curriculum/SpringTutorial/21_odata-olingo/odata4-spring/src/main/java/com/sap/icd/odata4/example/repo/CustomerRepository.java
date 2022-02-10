package com.sap.icd.odata4.example.repo;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.sap.icd.odata4.example.model.Customer;
import com.sap.icd.odata4.repo.ODataRepositoryExtension;

@RepositoryRestResource(collectionResourceRel = "customerSet", itemResourceRel = "customerEntity")
public interface CustomerRepository extends ODataRepositoryExtension<Customer, Long> {

}
