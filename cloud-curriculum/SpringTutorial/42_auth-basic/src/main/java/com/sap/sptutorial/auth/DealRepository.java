package com.sap.sptutorial.auth;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

@Repository
public class DealRepository {
    public final static String HASE_USER_ROLE = "hasRole('USER')";

    private Map<String, Deal> deals = Collections
            .synchronizedMap(new HashMap<>());
    
    
    public DealRepository() {
        deals.put("Jimmy", Deal.builder().id("Jimmy").title("Jimmy's Last Guitar").build());
    }

    @PreAuthorize(HASE_USER_ROLE)
    public Collection<Deal> findDeals(int offset, int count) {
        return deals.values();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void add(Deal deal) {
        deals.put(deal.getId(), deal);
    }

    @PreAuthorize("@bouncer.requestEntry()")
    public Deal findDeal(String id) {
        return deals.get(id);
    }

}
