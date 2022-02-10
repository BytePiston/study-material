package com.sap.sptutorial.validation.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=SpELParameterValidator.class)
public @interface ValidateParametersExpression {
    String message() default "Parameters not valid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String value();
}
