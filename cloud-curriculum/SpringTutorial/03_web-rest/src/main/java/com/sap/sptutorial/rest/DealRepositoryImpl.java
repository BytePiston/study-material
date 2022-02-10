package com.sap.sptutorial.rest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class DealRepositoryImpl implements DealRepository {
	private Map<String, Deal> deals = Collections.synchronizedMap(new HashMap<>());

	public DealRepositoryImpl() {
		deals.put("Jimmy", Deal.builder().id("Jimmy").title("Jimmy's Guitar").build());
	}

	@Override
	public DealCollection findAll(int offset, int count) {
		return new DealCollection(deals.values());
	}

	@Override
	public void add(Deal deal) {
		deals.put(deal.getId(), deal);
	}

	@Override
	public Deal find(String id) {
		return deals.get(id);
	}

	@Override
	public Long getVersion(String id) {
		Deal deal = deals.get(id);
		return (deal != null) ? deal.getVersion() : null;
	}

	@Override
	public DealPatch update(String id, DealPatch dealPatch) {
		Deal deal = find(id);
		if (deal != null) {
			dealPatch.getUpdateVector().entrySet().forEach((entry) -> {
				if (entry.getValue()) {
					String key = entry.getKey();
					switch (key) {
					case "title":
						deal.setTitle(dealPatch.getTitle());
						break;
					case "dealPriceAmount":
						deal.setDealPriceAmount(dealPatch.getDealPriceAmount());
						break;
					case "normalPriceAmount":
						deal.setNormalPriceAmount(dealPatch.getNormalPriceAmount());
						break;
					case "description":
						deal.setDescription(dealPatch.getDescription());
						break;
					case "redeemableFrom":
						deal.setRedeemableFrom(dealPatch.getRedeemableFrom());
						break;
					case "redeemableTo":
						deal.setRedeemableTo(dealPatch.getRedeemableTo());
						break;
					}
					deal.incrementVersion();
				}
			});
			return dealPatch;
		}
		return null;
	}

	@Override
	public boolean exists(String id) {
		return deals.containsKey(id);
	}
}
