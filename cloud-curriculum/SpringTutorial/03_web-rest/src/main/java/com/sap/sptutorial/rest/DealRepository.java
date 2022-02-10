package com.sap.sptutorial.rest;

public interface DealRepository {
	DealCollection findAll(int offset, int count);

	Deal find(String id);
	
	boolean exists(String id);

	void add(Deal deal);
	
	Long getVersion(String id);

	DealPatch update(String id, DealPatch dealPatch);
}
