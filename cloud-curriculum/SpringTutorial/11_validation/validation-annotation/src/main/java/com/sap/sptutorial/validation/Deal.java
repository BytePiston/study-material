package com.sap.sptutorial.validation;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Getter
@Setter
public class Deal {
	@Pattern(regexp = "D_[A-Z]{2}[0-9]{4}")
	private String id;

	@NotBlank
	private String title;
	
	private String description;

	@DecimalMin("0.0")
	private BigDecimal dealPriceAmount;

	@DecimalMin("0.0")
	private BigDecimal normalPriceAmount;

	@DecimalMin("0.0")
	@DecimalMax("100.0")
	@Digits(integer = 3, fraction = 2)
	private BigDecimal percentDiscount;

	@Min(0)
	@NotNull
	private Long numberAvailable;

	@Min(0)
	private Long availablePerCustomer = new Long(1);

	private Boolean featured = false;

	@Future
	private Date redeemableFrom;

	@Future
	private Date redeemableTo;

	@Valid
	private DealCategory category;

	private Status status = Status.DRAFT;

	@Min(value=0, message="better give me 5!")
	private Long version;

	@AssertTrue(message="Customers can only get the max number available")
	public boolean isValid() {
		return (availablePerCustomer != null && numberAvailable != null && availablePerCustomer <= numberAvailable);
	}

	public enum Status {
		DRAFT, APPROVAL_PENDING, SUBMITTED, ACTIVE, EXPIRED, SUSPENDED, SOLDOUT
	}
}
