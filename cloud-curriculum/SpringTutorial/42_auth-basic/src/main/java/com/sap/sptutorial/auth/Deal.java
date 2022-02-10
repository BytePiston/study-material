package com.sap.sptutorial.auth;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
