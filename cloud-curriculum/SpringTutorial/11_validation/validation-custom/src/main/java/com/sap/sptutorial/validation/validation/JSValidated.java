package com.sap.sptutorial.validation.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = JSConstraintValidator.class)
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface JSValidated {
	String script();
	String function() default "";
	String message() default "invalid object";
	Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
