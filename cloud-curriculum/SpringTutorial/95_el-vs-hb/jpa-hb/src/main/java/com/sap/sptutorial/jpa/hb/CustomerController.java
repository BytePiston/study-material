package com.sap.sptutorial.jpa.hb;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sap.sptutorial.jpa.hb.customer.dao.CustomerDao;
import com.sap.sptutorial.jpa.hb.customer.model.Customer;

@RestController
@RequestMapping("customer")
public class CustomerController {

	private CustomerService customerService;
	private CustomerDao customerDao;

	@Autowired
	public CustomerController(CustomerService customerService, CustomerDao customerDao) {
		this.customerService = customerService;
		this.customerDao = customerDao;
	}

	@RequestMapping(path = "{id}", method = RequestMethod.PATCH)
	public void updateCustomer(@RequestBody @Validated Customer customer, @PathParam("id") Long id) {
		customerService.updateCustomer(id, customer);
	}

	@RequestMapping(path = "testPC", method = RequestMethod.GET)
	public void testPersistenceContext() {
		Customer c1 = new Customer();
		c1.setDisplayName("customer1");
		customerDao.save(c1);
		Customer c2 = customerDao.findByDisplayName("customer1").get(0);
		c2.setDisplayName("changed");
		Customer c3 = new Customer();
		c3.setDisplayName("customer2");
		customerDao.save(c3);
	}
}
