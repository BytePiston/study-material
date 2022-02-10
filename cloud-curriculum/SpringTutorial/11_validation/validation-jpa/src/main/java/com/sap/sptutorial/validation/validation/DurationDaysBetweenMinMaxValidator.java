package com.sap.sptutorial.validation.validation;

import java.time.Duration;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DurationDaysBetweenMinMaxValidator implements ConstraintValidator<DurationDaysBetweenMinMax, Duration> {
	private Duration minDays;
	private Duration maxDays;

	@Override
	public void initialize(DurationDaysBetweenMinMax constraintAnnotation) {
		minDays = Duration.ofDays(constraintAnnotation.minDays());
		maxDays = constraintAnnotation.maxDays() > 0 ? Duration.ofDays(constraintAnnotation.maxDays()) : null;
	}

	@Override
	public boolean isValid(Duration value, ConstraintValidatorContext context) {
		return value != null && value.compareTo(minDays) >= 0 && value.compareTo(maxDays) <= 0;
	}

}
