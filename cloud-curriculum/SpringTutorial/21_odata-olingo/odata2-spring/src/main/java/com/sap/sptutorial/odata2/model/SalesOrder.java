package com.sap.sptutorial.odata2.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.sap.icd.odatav2.spring.annotations.ModelProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "SALES_ORDER")
public class SalesOrder {
	@Id
	@Column(name = "ID")
	private Long id;

	@Column(name = "TITLE")
	private String title;

	@ManyToOne
	@JoinColumn(name = "CUSTOMER_ID")
	private Customer customer;
	
    @Transient
    @ModelProperty("computed")
    private String customerName;
    
    public String getCustomerName() {
    	return customer.getDisplayName();
    }
}
