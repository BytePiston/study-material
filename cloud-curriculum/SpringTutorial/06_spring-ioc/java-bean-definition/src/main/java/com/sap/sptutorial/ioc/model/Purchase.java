package com.sap.sptutorial.ioc.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Purchase {
	private Customer customer;

	public Purchase(Customer customer) {
		this.customer = customer;
	}
}
