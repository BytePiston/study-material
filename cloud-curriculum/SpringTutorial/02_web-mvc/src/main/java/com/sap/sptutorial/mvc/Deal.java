package com.sap.sptutorial.mvc;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="id")
@ToString
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
