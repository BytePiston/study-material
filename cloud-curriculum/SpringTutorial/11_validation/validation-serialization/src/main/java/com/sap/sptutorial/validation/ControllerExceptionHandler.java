package com.sap.sptutorial.validation;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;
import lombok.Setter;

@ControllerAdvice(assignableTypes={DealControllerEx2.class})
public class ControllerExceptionHandler {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Errors handleException(MethodArgumentNotValidException ex) {
		Errors errors = new Errors();
		errors.setException(ex.getClass().getCanonicalName());
		for (ObjectError error : ex.getBindingResult().getAllErrors()) {
			errors.add(error.getDefaultMessage());
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
