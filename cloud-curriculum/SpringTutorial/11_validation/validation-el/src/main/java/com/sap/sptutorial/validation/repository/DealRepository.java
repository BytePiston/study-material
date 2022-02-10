package com.sap.sptutorial.validation.repository;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import com.sap.sptutorial.validation.groups.FinalChecks;
import com.sap.sptutorial.validation.model.Deal;
import com.sap.sptutorial.validation.validator.ValidateParametersExpression;

@Component
@Validated
public class DealRepository {
    private Map<String, Deal> deals = Collections
            .synchronizedMap(new HashMap<>());

    @Validated({ FinalChecks.class })
    public void put(@Valid Deal deal) {
        deals.put(deal.getId(), deal);
    }

    public Collection<Deal> getAll() {
        return deals.values();
    }

    @ValidateParametersExpression("#param00.before(#param01)")
    public Collection<Deal> getAllFromTo(Date from, Date to) {
        return deals.values().stream()
                .filter(deal -> deal.getRedeemableFrom().after(from)
                        && deal.getRedeemableTo().before(to))
                .collect(Collectors.toList());
    }

    public Deal get(String id) {
        return deals.get(id);
    }
}
