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
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Getter
@Setter
public class Deal {
    @Pattern(regexp = "D_[A-Z][A-Z][0-9]{4}", groups = FinalChecks.class)
	@NotNull(groups = FinalChecks.class)
	@Null(groups = InputChecks.class)
	private String id;

    @NotEmpty(groups = InputChecks.class)
    private String title;

    private String description;

    @DecimalMin(value = "0.0", groups = InputChecks.class)
    private BigDecimal dealPriceAmount;

    @DecimalMin(value = "0.0", groups = InputChecks.class)
    private BigDecimal normalPriceAmount;

    @DecimalMin(value = "0.0", groups = InputChecks.class)
    @DecimalMax(value = "100.0", groups = InputChecks.class)
    @Digits(integer = 3, fraction = 2, groups = InputChecks.class)
    private BigDecimal percentDiscount;

    @Min(value = 0, groups = InputChecks.class)
    @NotNull
    private Long numberAvailable;

    @Min(value = 0, groups = InputChecks.class)
    private Long availablePerCustomer = new Long(1);

    private Boolean featured = false;

    @Future
    private Date redeemableFrom;

    @Future
    private Date redeemableTo;

    @Valid
    private DealCategory category;

    private Status status = Status.DRAFT;

    private Long version;

    @AssertTrue(groups = FinalChecks.class, message = "Deals available per customer must be smaller then or equal to the overall available deals")
    public boolean isValid() {
        return (availablePerCustomer <= numberAvailable);
    }

    public enum Status {
        DRAFT, APPROVAL_PENDING, SUBMITTED, ACTIVE, EXPIRED, SUSPENDED, SOLDOUT
    }
}
