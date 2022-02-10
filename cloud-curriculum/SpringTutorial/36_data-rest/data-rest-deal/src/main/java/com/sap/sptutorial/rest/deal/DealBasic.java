package com.sap.sptutorial.rest.deal;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

@Projection(name="dealBasic", types=Deal.class)
public interface DealBasic {
	public BigDecimal getDealPriceAmount();
	public BigDecimal getNormalPriceAmount();
	public Long getNumberAvailable();
	
	@Value("#{target.title}")
	public String getAnotherTitle();
	
	@Value("#{target.category.name}")
	public String getCategory();
	
	@Value("#{@hello.getGreeting()}")
	public String getHello(); 
}
