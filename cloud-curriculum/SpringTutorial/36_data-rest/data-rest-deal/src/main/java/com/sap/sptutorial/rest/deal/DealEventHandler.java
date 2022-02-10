package com.sap.sptutorial.rest.deal;

import java.time.LocalDateTime;

import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@RepositoryEventHandler
@Component
@Validated
public class DealEventHandler {
	@HandleBeforeSave
	public void handleDealSave(@Validated Deal deal) {
		deal.setLastModified(LocalDateTime.now());
	}
	
	@HandleBeforeCreate
	public void handleDealCreate(Deal deal) {
		deal.setLastModified(LocalDateTime.now());
	}
}
