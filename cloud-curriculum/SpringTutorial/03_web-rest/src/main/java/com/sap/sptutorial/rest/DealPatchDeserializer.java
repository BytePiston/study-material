package com.sap.sptutorial.rest;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.boot.jackson.JsonComponent;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

@JsonComponent
public class DealPatchDeserializer extends JsonDeserializer<DealPatch> {
	@Override
	public DealPatch deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		DealPatch dealPatch = new DealPatch();
		JsonNode root = p.readValueAsTree();

		Deal deal = p.getCodec().treeToValue(root, Deal.class);

		JsonNode titleNode = root.path("title");
		update(dealPatch::setTitle, deal::getTitle, titleNode);

		JsonNode dealPriceAmountNode = root.path("dealPriceAmount");
		update(dealPatch::setDealPriceAmount, deal::getDealPriceAmount, dealPriceAmountNode);

		JsonNode descriptionNode = root.path("description");
		update(dealPatch::setDescription, deal::getDescription, descriptionNode);

		JsonNode normalPriceAmountNode = root.path("normalPriceAmount");
		update(dealPatch::setNormalPriceAmount, deal::getNormalPriceAmount, normalPriceAmountNode);

		JsonNode redeemableFromNode = root.path("redeemableFrom");
		update(dealPatch::setRedeemableFrom, deal::getRedeemableFrom, redeemableFromNode);

		JsonNode redeemableToNode = root.path("redeemableTo");
		update(dealPatch::setRedeemableTo, deal::getRedeemableTo, redeemableToNode);

		return dealPatch;
	}

	private <T> void update(Consumer<T> setter, Supplier<T> value, JsonNode node) {
		if (!node.isMissingNode()) {
			if (node.isNull()) {
				setter.accept(null);
			} else {
				setter.accept(value.get());
			}
		}
	}
}
