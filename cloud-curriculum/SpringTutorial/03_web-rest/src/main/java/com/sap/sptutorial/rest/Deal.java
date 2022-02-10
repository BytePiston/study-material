package com.sap.sptutorial.rest;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

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

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
	private Date redeemableFrom;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
	private Date redeemableTo;

	private Long version;

	public void incrementVersion() {
		version++;
	}
}
