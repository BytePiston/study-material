package com.sap.sptutorial.validation;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sap.sptutorial.validation.serialization.DurationAsDaysDeserializer;
import com.sap.sptutorial.validation.serialization.DurationAsDaysSerializer;
import com.sap.sptutorial.validation.validation.DurationDaysBetweenMinMax;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Deal {

    @Pattern(regexp = "D_[A-Z][A-Z][0-9]{4}", groups = { PrePersist.class })
    @NotNull
    @Id
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
    @Digits(integer = 3, fraction = 2)
    private BigDecimal percentDiscount;

    @Min(value = 0, groups = PrePersist.class)
    @NotNull
    private Long numberAvailable;

    @Min(0)
    private Long availablePerCustomer = new Long(1);

    private Boolean featured = false;

    @JsonSerialize(using = DurationAsDaysSerializer.class)
    @JsonDeserialize(using = DurationAsDaysDeserializer.class)
    @DurationDaysBetweenMinMax(minDays = 0, maxDays = 7)
    private Duration daysRedeemable;

    @NotNull
    private ZonedDateTime redeemableFrom;

    @Valid
    @ManyToOne
    private DealCategory category;

    private Status status = Status.DRAFT;

    @Min(0)
    private Long version;
    
    @AssertTrue(groups = PrePersist.class, message = "Deals available per customer must be smaller then or equal to the overall available deals")
    public boolean isValid() {
        return (availablePerCustomer <= numberAvailable);
    }

    public enum Status {
        DRAFT, APPROVAL_PENDING, SUBMITTED, ACTIVE, EXPIRED, SUSPENDED, SOLDOUT
    }
}
