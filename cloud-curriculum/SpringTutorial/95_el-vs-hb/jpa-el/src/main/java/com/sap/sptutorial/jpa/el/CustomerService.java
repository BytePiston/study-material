package com.sap.sptutorial.jpa.el;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sap.sptutorial.jpa.el.customer.dao.CustomerDao;
import com.sap.sptutorial.jpa.el.customer.model.Customer;

@Service
public class CustomerService {
	CustomerDao customerDao;
	
	@Autowired
	public CustomerService(CustomerDao customerDao) {
		this.customerDao = customerDao;
	}
	
	public void updateCustomer(Long id, Customer customer) {
		Customer existing = customerDao.findOne(id);
		if(existing == null){
			throw new IllegalArgumentException("No customer found with id: " + id);
		}
		if(customer.getDisplayName() != null && !customer.getDisplayName().isEmpty()) {
			existing.setDisplayName(customer.getDisplayName());
			customerDao.save(existing);
		}
	}
}
