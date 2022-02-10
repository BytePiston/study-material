package com.sap.sptutorial.authentication.saml.test;

import java.util.ArrayList;
import java.util.List;

import org.opensaml.saml2.core.Attribute;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.XSString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserDetailsService implements SAMLUserDetailsService {
	@Override
	public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
		String userId = credential.getNameID().getValue();
		log.info("SAML Authentication User ID: " + userId);
		List<Attribute> attributes = credential.getAttributes();
		for (Attribute attribute : attributes) {
			log.info("SAML Assertion Attribute: " + attribute.getName());
			List<XMLObject> values = attribute.getAttributeValues();
			for (XMLObject value : values) {
				if (value instanceof XSString) {
					log.info("    Value: " + ((XSString) value).getValue());
				}
			}
		}
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
		authorities.add(authority);

		// In a real scenario, this implementation has to locate user in a
		// arbitrary
		// dataStore based on information present in the SAMLCredential and
		// returns such a date in a form of application specific UserDetails
		// object.
		return new User(userId, "<abc123>", true, true, true, true, authorities);
	}
}
