package com.sap.sptutorial.validation;

import java.util.Collection;

import javax.validation.Validator;
import javax.validation.groups.Default;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping("deal_ex1")
public class DealControllerEx1 {

    @Autowired
    private DealList deals;

    @Autowired
    private Validator validator;

    @PostMapping
    public ResponseEntity<?> createDeal(
            @RequestBody @Validated(InputChecks.class) Deal deal)
            throws NoSuchMethodException, SecurityException,
            MethodArgumentNotValidException {
        deal.setId("D_AB1234");

        SpringValidatorAdapter v = new SpringValidatorAdapter(validator);
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(deal,
                "deal");
        v.validate(deal, errors, FinalChecks.class, Default.class);
        if (errors.hasErrors()) {
            throw new MethodArgumentNotValidException(
                    new MethodParameter(this.getClass()
                            .getDeclaredMethod("createDeal", Deal.class), 0),
                    errors);
        }

        deals.put(deal);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/" + deal.getId()).build().toUri());
        return new ResponseEntity<>(null, headers, HttpStatus.CREATED);
    }

    @GetMapping
    public Collection<Deal> getAllDeals() {
        return deals.getAll();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleException(
            MethodArgumentNotValidException ex) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode object = mapper.createObjectNode();
        ArrayNode errors = object.putArray("errors");
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            errors.add(error.getDefaultMessage());
        }
        return new ResponseEntity<>(object, HttpStatus.BAD_REQUEST);
    }
}
