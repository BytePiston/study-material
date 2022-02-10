package com.sap.sptutorial.rest.deal;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.hateoas.Resource;

@Configuration
public class DealRepositoryRestConfigurer extends RepositoryRestConfigurerAdapter {
	@Override
	public void configureConversionService(ConfigurableConversionService conversionService) {
		conversionService.removeConvertible(Deal.class, Resource.class);
		conversionService.addConverter(new DealConverter());
	}
}
