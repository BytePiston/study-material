package com.sap.sptutorial.rest;

import java.util.Date;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = DealPatchDeserializer.class)
@JsonIgnoreProperties(value = { "id", "version", "title", "dealPriceAmount", "normalPriceAmount", "description",
		"redeemableFrom", "redeemableTo"})
public class DealPatch extends Deal {
	private HashMap<String, Boolean> updateVector = new HashMap<>();

	public HashMap<String, Boolean> getUpdateVector() {
		return updateVector;
	}

	@Override
	public void setDealPriceAmount(Double dealPriceAmount) {
		super.setDealPriceAmount(dealPriceAmount);
		updateVector.put("dealPriceAmount", true);
	}

	@Override
	public void setDescription(String description) {
		super.setDescription(description);
		updateVector.put("description", true);
	}

	@Override
	public void setNormalPriceAmount(Double normalPriceAmount) {
		super.setNormalPriceAmount(normalPriceAmount);
		updateVector.put("normalPriceAmmount", true);
	}

	@Override
	public void setRedeemableFrom(Date redeemableFrom) {
		super.setRedeemableFrom(redeemableFrom);
		updateVector.put("redeemableFrom", true);
	}

	@Override
	public void setRedeemableTo(Date redeemableTo) {
		super.setRedeemableTo(redeemableTo);
		updateVector.put("redeemableTo", true);
	}

	@Override
	public void setTitle(String title) {
		super.setTitle(title);
		updateVector.put("title", true);
	}
}
