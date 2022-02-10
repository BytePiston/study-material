package com.sap.sptutorial.rest;

import java.util.ArrayList;
import java.util.Collection;

import lombok.Data;

@Data
public class DealCollection {
	final private ArrayList<Deal> deals;

	public DealCollection() {
		deals = new ArrayList<>();
	}

	public DealCollection(Collection<Deal> coll) {
		deals = new ArrayList<>(coll);
	}

	public void add(Deal deal) {
		deals.add(deal);
	}
}
