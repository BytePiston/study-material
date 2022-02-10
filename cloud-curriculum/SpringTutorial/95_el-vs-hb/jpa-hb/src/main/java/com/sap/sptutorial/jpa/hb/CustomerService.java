package com.sap.sptutorial.jpa.hb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sap.sptutorial.jpa.hb.customer.dao.CustomerDao;
import com.sap.sptutorial.jpa.hb.customer.model.Customer;

@Service
public class CustomerService {
	CustomerDao customerDao;

	@Autowired
	public CustomerService(CustomerDao customerDao) {
		this.customerDao = customerDao;
	}

	public void updateCustomer(Long id, Customer customer) {
		customer.setId(id);
		customerDao.save(customer);
	}
}
