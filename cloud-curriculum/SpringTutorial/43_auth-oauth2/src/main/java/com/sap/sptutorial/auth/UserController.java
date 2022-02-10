package com.sap.sptutorial.auth;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
	@GetMapping("/user")
	public Principal user(Principal principal) {
		System.out.println(principal);
		return principal;
	}
}
