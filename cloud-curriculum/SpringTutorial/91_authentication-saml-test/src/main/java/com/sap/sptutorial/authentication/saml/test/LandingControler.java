package com.sap.sptutorial.authentication.saml.test;

import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LandingControler {
	@RequestMapping("/landing")
	public String landing(@CurrentUser User user, Model model) {
		model.addAttribute("username", user.getUsername());
		return "landing";
	}
}
