package com.sap.sptutorial.validation.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = DurationDaysBetweenMinMaxValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DurationDaysBetweenMinMax {
	int minDays() default 0;
	int maxDays() default 0;
	String message() default "must be between {minDays} and {maxDays} days";
	Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
