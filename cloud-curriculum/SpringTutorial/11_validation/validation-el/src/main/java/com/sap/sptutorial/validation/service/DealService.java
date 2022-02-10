package com.sap.sptutorial.validation.service;

import java.util.Collection;
import java.util.Date;

import javax.validation.Valid;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import com.sap.sptutorial.validation.groups.InputChecks;
import com.sap.sptutorial.validation.model.Deal;
import com.sap.sptutorial.validation.repository.DealRepository;

@Component
@Validated
public class DealService {
    private final DealRepository repository;

    public DealService(DealRepository repository) {
        this.repository = repository;
    }

    @Validated({ InputChecks.class })
    public void put(@Valid Deal deal) {
        deal.setId("D_AB1234");
        repository.put(deal);
    }

    public Collection<Deal> getAll() {
        return repository.getAll();
    }

    public Deal get(String id) {
        return repository.get(id);
    }

    public Collection<Deal> getAllFromTo(Date from, Date to) {
        return repository.getAllFromTo(from, to);
    }
}
