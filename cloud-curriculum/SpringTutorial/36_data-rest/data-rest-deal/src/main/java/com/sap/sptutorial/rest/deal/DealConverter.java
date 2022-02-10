package com.sap.sptutorial.rest.deal;

import org.springframework.core.convert.converter.Converter;
import org.springframework.hateoas.Resource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DealConverter implements Converter<Deal, Resource<?>> {
	@Override
	public Resource<Deal> convert(Deal source) {
		log.info("Converter");
		return null;
	}
}
