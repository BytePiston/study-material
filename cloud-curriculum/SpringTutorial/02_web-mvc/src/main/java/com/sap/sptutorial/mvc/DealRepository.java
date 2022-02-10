package com.sap.sptutorial.mvc;

import java.util.List;

public interface DealRepository {
	List<Deal> findDeals(int offset, int count);
	void add(Deal deal);
}
