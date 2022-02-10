package com.sap.sptutorial.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.Getter;

@RestController
@RequestMapping("deal")
public class DealController {
	@Autowired
	private DealRepository deals;

	@PostMapping
	public ResponseEntity<?> createDeal(@RequestBody Deal deal)
			throws NoSuchMethodException, SecurityException, MethodArgumentNotValidException {
		deal.setId("D_AB1234");
		deals.save(deal);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/" + deal.getId()).build().toUri());
		return new ResponseEntity<>(null, headers, HttpStatus.CREATED);
	}

	@GetMapping
	public Iterable<Deal> getAllDeals() {
		return deals.findAll();
	}
	
	@GetMapping(path="findByTitle")
	public Iterable<Deal> getByTitle(@RequestParam String title) {
		return deals.findByTitleIgnoreCase(title);
	}

	@ExceptionHandler(TransactionSystemException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Errors handleConstraintViolations(TransactionSystemException ex) {
		Errors errors = new Errors();
		Throwable rootCause = ex.getRootCause();
		if(rootCause instanceof ConstraintViolationException) {
		((ConstraintViolationException)rootCause).getConstraintViolations().forEach(violation -> {
			errors.add(violation.getPropertyPath() + ":" + violation.getMessage());
		});
		} else {
			errors.violations.add(ex.getLocalizedMessage());
		}
		return errors;
	}

	class Errors {
		@Getter
		private List<String> violations = new ArrayList<>();

		void add(String violation) {
			violations.add(violation);
		}
	}
}
