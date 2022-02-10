package com.sap.sptutorial.validation.controller;

import java.util.ArrayList;
import java.util.Collection;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;
import lombok.Setter;

@ControllerAdvice
public class ControllerExceptionHandler {
	
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Errors handleException(ConstraintViolationException ex) {
		Errors errors = new Errors();
		errors.setException(ex.getClass().getCanonicalName());
		for (ConstraintViolation<?> error : ex.getConstraintViolations()) {
			errors.add(error.getMessage());
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
