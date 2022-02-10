package com.sap.sptutorial.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("uimodel")
public class UIModelController {
	private UIModel model;

	public UIModelController(UIModel model) {
		this.model = model;
	}

	@GetMapping(produces = "application/json")
	public UIModel getModel() {
		return model;
	}
}
