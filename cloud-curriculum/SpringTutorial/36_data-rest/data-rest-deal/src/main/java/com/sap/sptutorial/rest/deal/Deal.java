package com.sap.sptutorial.rest.deal;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Entity
public class Deal {
	@Getter
	@Setter
	@NotNull
	@Id
	private Long id;

	@Getter
	@Setter
	@NotEmpty(groups=PrePersistValidation.class)
	private String title;

	@Getter
	@Setter
	private String description;

	@Getter
	@Setter
	@DecimalMin("0.0")
	private BigDecimal dealPriceAmount;

	@Getter
	@Setter
	@DecimalMin("0.0")
	private BigDecimal normalPriceAmount;

	@Getter
	@Setter
	@DecimalMin("0.0")
	@DecimalMax("100.0")
	@Digits(integer = 3, fraction = 2)
	private BigDecimal percentDiscount;

	@Getter
	@Setter
	@Min(0)
	@NotNull
	private Long numberAvailable;

	@Getter
	@Setter
	@Min(0)
	private Long availablePerCustomer = new Long(1);

	@Getter
	@Setter
	private Boolean featured = false;

	@Getter
	@Setter
	@NotNull
	private ZonedDateTime redeemableFrom;
	
	@Getter
	@Setter
	private ZonedDateTime redeemableTo;

	@Getter
	@Setter
	@Valid
	@ManyToOne
	private DealCategory category;

	@Getter
	@Setter
	private Status status = Status.DRAFT;

	@Getter
	@Setter
	@Min(0)
	@Version
	private Long version;
	
	@Getter
	@Setter
	@LastModifiedDate
	private LocalDateTime lastModified;

	public enum Status {
		DRAFT, APPROVAL_PENDING, SUBMITTED, ACTIVE, EXPIRED, SUSPENDED, SOLDOUT
	}
}
