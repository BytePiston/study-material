package com.sap.sptutorial.validation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class DealList {
	private Map<String, Deal> deals = new HashMap<>();

	public void put(Deal deal) {
		deals.put(deal.getId(), deal);
	}

	public Collection<Deal> getAll() {
		return deals.values();
	}

	public Deal get(String id) {
		return deals.get(id);
	}
}
