package com.sap.sptutorial.ioc.model;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString
@Slf4j
public class Purchase {

	private Customer customer;
	private Product product;
	private Channel channel;

	public Purchase(Customer customer) {
	    log.info("injecting Customer into Purchase");
		this.customer = customer;
	}
	
	public void setProduct(Product product) {
	    log.info("injecting Product into Purchase");
	    this.product = product;
	}
	
	public void setChannel(Channel channel) {
	    log.info("injecting Channel into Purchase");
	    this.channel = channel;
	}
}
