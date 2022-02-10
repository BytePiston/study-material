package com.sap.sptutorial.authentication.saml;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.metadata.MetadataManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/saml")
public class SamlController {
	@Autowired
	private MetadataManager metadata;

	@Value("${sap.failure.url}")
	private String failureUrl;

	@Value("${sap.landing.url}")
	private String landingUrl;

	@Value("${sap.logout.url")
	private String logoutUrl;

	@RequestMapping(value = "/idpSelection", method = RequestMethod.GET)
	public String idpSelection(HttpServletRequest request, Model model) {
		if (!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
			log.warn("The current user is already logged.");
			return "redirect:" + landingUrl;
		} else {
			if (isForwarded(request)) {
				Set<String> idps = metadata.getIDPEntityNames();
				for (String idp : idps)
					log.info("Configured Identity Provider for SSO: " + idp);
				model.addAttribute("idps", idps);
				return "saml/idpselection";
			} else {
				log.warn("Direct accesses to '/idpSelection' route are not allowed");
				return "redirect:" + logoutUrl;
			}
		}
	}

	/*
	 * Checks if an HTTP request is forwarded from servlet.
	 */
	private boolean isForwarded(HttpServletRequest request) {
		return request.getAttribute("javax.servlet.forward.request_uri") != null;
	}
}
