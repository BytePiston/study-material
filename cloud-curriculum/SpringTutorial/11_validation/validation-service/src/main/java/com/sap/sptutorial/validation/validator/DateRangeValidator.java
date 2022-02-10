package com.sap.sptutorial.validation.validator;

import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;

@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class DateRangeValidator
        implements ConstraintValidator<ValidDateRange, Object[]> {

    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isValid(Object[] params, ConstraintValidatorContext context) {
        if(params.length != 2) {
            throw new IllegalArgumentException("Only methods with exactly 2 arguments are supported");
        } else if (params[0] == null || params[1] == null) {
            throw new IllegalArgumentException("Parameters must not be null");
        } else if (!(params[0] instanceof Date) || !(params[1] instanceof Date)) {
            throw new IllegalArgumentException("Parameters must be of type Date");
        } else  {
            Date date1 = (Date) params[0];
            Date date2 = (Date) params[1];
            return date1.before(date2);
        }
    }

}
