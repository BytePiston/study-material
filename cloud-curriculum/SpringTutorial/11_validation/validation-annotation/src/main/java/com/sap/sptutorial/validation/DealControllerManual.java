package com.sap.sptutorial.validation;

import java.util.Collection;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("deal_manual")
@Slf4j
public class DealControllerManual {
	
	private DealList deals;
    private Validator validator;

    @Autowired
    public DealControllerManual(DealList deals, Validator validator) {
        this.deals = deals;
        this.validator = validator;
    }

	@PostMapping
	public ResponseEntity<?> createDeal(@RequestBody Deal deal) {
		Set<ConstraintViolation<Deal>> constraintViolations = validator.validate(deal);

		if (!constraintViolations.isEmpty()) {
			for (ConstraintViolation<Deal> constraintViolation : constraintViolations) {
				log.info(constraintViolation.getMessage());
			}
			throw new IllegalArgumentException("Deal object submitted is not valid");
		}
		deals.put(deal);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/" + deal.getId()).build().toUri());
		return new ResponseEntity<>(null, headers, HttpStatus.CREATED);
	}

	@GetMapping
	public Collection<Deal> getAllDeals() {
		return deals.getAll();
	}
}
