package com.sap.sptutorial.rest.deal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ControllerAdvice
public class ControllerExceptionHandler {

	@ExceptionHandler({ JpaSystemException.class, TransactionSystemException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Errors handleException(Throwable ex) {
		Errors errors = new Errors();
		while (ex != null) {
			if (ex instanceof ConstraintViolationException) {
				Set<ConstraintViolation<?>> violations = ((ConstraintViolationException) ex).getConstraintViolations();
				for (ConstraintViolation<?> violation : violations) {
					ValidationError error = new ValidationError(violation);
					errors.add(error.toString());
				}
			} else {
				errors.add(ex.getClass().getCanonicalName() + ": " + ex.getLocalizedMessage());
			}
			ex = ex.getCause();
		}
		return errors;
	}
}

class Errors {
	@Getter
	private Collection<String> errors = new ArrayList<>();

	@Getter
	@Setter
	private String exception;

	void add(String error) {
		errors.add(error);
	}
}

@ToString
class ValidationError {
	public ValidationError(ConstraintViolation<?> violation) {
		rootBeanClass = violation.getRootBeanClass().getCanonicalName();
		propertyPath = violation.getPropertyPath().toString();
		message = violation.getMessage();
		messageTemplate = violation.getMessageTemplate();
	}
	@Getter
	@Setter
	private String rootBeanClass;
	@Getter
	@Setter
	private String propertyPath;
	@Getter
	@Setter
	private String message;
	@Getter
	@Setter
	private String messageTemplate;
}
