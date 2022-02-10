package com.sap.sptutorial.supportability.metrics;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Getter
@Setter
public class Deal {
	private String id;
	private String title;
	private String description;
	private Double dealPriceAmount;
	private Double normalPriceAmount;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date redeemableFrom;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date redeemableTo;
}
