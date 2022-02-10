package com.sap.sptutorial.validation.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy= {DateRangeValidator.class})
public @interface ValidDateRange {
    String message() default "Start date must be earlier then end date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
