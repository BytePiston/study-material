package com.sap.sptutorial.validation;

import java.math.BigDecimal;
import java.time.Duration;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sap.sptutorial.validation.serialization.DurationAsDaysDeserializer;
import com.sap.sptutorial.validation.serialization.DurationAsDaysSerializer;

import lombok.Data;

@Data
public class Deal {
	@Pattern(regexp= "D_[A-Z][A-Z][0-9]{4}")
	@NotNull(groups=FinalChecks.class)
	@Null(groups=InputChecks.class)
	private String id;

	@NotEmpty
	private String title;

	private String description;

	@DecimalMin("0.0")
	private BigDecimal dealPriceAmount;

	@DecimalMin("0.0")
	private BigDecimal normalPriceAmount;

	@DecimalMin("0.0")
	@DecimalMax("100.0")
	@Digits(integer=3, fraction=2)
	private BigDecimal percentDiscount;

	@Min(0)
	@NotNull
	private Long numberAvailable;

	@Min(0)
	private Long availablePerCustomer = new Long(1);

	private Boolean featured = false;

	@JsonSerialize(using=DurationAsDaysSerializer.class)
	@JsonDeserialize(using=DurationAsDaysDeserializer.class)
	private Duration daysRedeemable;

	@Valid
	private DealCategory category;

	private Status status = Status.DRAFT;

	@Min(0)
	private Long version;
	
	@AssertTrue(groups=FinalChecks.class, message="{deal.available}")
	public boolean isCorrectAvailablePerCustomer() {
		return (availablePerCustomer <= numberAvailable);
	}

	public enum Status {
		DRAFT, APPROVAL_PENDING, SUBMITTED, ACTIVE, EXPIRED, SUSPENDED, SOLDOUT
	}
}
