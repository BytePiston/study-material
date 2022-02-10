package com.sap.sptutorial.validation;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;

import javax.validation.Valid;
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
import com.sap.sptutorial.validation.validation.DurationDaysBetweenMinMax;
import com.sap.sptutorial.validation.validation.JSValidated;
import com.sap.sptutorial.validation.validation.JSValidatedField;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@JSValidated(script = "static/DealValidator.js", function = "checkDeal", groups = FinalChecks.class)
public class Deal {

    @Pattern(regexp = "D_[A-Z][A-Z][0-9]{4}")
    @NotNull(groups = FinalChecks.class)
    @Null(groups = InputChecks.class)
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

    @Min(0)
    @NotNull
    private Long numberAvailable;

    @Min(0)
    private Long availablePerCustomer = new Long(1);

    private Boolean featured = false;

    @JsonSerialize(using = DurationAsDaysSerializer.class)
    @JsonDeserialize(using = DurationAsDaysDeserializer.class)
    @DurationDaysBetweenMinMax(minDays = 0, maxDays = 7, groups = InputChecks.class)
    private Duration daysRedeemable;

    @NotNull(groups = FinalChecks.class)
    @JSValidatedField(check = "checkRedeemableFrom", message = "redeemableFrom must start on second of a month")
    private ZonedDateTime redeemableFrom;

    @Valid
    private DealCategory category;

    private Status status = Status.DRAFT;

    @Min(0)
    private Long version;

    public enum Status {
        DRAFT, APPROVAL_PENDING, SUBMITTED, ACTIVE, EXPIRED, SUSPENDED, SOLDOUT
    }
}
