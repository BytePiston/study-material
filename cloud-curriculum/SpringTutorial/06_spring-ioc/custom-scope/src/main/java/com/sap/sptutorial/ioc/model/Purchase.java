package com.sap.sptutorial.ioc.model;

import java.time.ZonedDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Component
@Scope(scopeName = "purchase")
public class Purchase {

	private Customer customer;
	private ZonedDateTime startTime;
	private String id;

	public Purchase(String id) {
		this.id = id;
		this.startTime = ZonedDateTime.now();
	}

	@Autowired
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public void initFromYaml(Map<String, Object> content) {
		Object object = content.get("customer");
		if (object instanceof Map) {
			Map<?, ?> customerMap = (Map<?, ?>) object;
			customer.setName((String) customerMap.get("name"));
		}
	}
}
