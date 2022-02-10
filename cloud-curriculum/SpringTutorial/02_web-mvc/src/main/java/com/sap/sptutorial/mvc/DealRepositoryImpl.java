package com.sap.sptutorial.mvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class DealRepositoryImpl implements DealRepository {
	private List<Deal> deals = Collections.synchronizedList(new ArrayList<>());

	@Override
	public List<Deal> findDeals(int offset, int count) {
		return deals;
	}

	@Override
	public void add(Deal deal) {
		deals.add(deal);
	}

}
