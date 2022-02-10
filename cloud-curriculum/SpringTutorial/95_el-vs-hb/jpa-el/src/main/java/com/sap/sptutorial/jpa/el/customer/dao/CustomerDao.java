package com.sap.sptutorial.jpa.el.customer.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sap.sptutorial.jpa.el.customer.model.Customer;

@Repository
public interface CustomerDao extends PagingAndSortingRepository<Customer, Long> {
    List<Customer> findByDisplayName(@Param("name") String name);
}
