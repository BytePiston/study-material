package com.sap.sptutorial.authentication.saml.test;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.key.JKSKeyManager;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.sap.sptutorial.authentication.saml.SapSamlConfig;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends SapSamlConfig {
	private KeyManager keyManager;

	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private SAMLAuthenticationProvider samlAuthenticationProvider;
	
	@Autowired
	private FilterChainProxy samlFilter;
	
	@Autowired
	private SAMLEntryPoint samlEntryPoint;

	@Override
	protected UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

	@Value("${sap.failure.url}")
	private String failureUrl;

	@Value("${sap.logout.url}")
	private String logoutUrl;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic().authenticationEntryPoint(samlEntryPoint);
		http.csrf().disable();
		http.addFilterBefore(metadataGeneratorFilter(), ChannelProcessingFilter.class)
				.addFilterAfter(samlFilter, BasicAuthenticationFilter.class);
		http.authorizeRequests().antMatchers(logoutUrl).permitAll().antMatchers(failureUrl).permitAll()
				.antMatchers(getSamlUrlPattern()).permitAll().anyRequest().authenticated();
		http.logout().logoutSuccessUrl(logoutUrl);
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(samlAuthenticationProvider).inMemoryAuthentication().withUser("test")
				.password("test").roles("User");
	}

	@Override
	@Bean
	public KeyManager getKeyManager() {
		if (keyManager == null) {
			DefaultResourceLoader loader = new DefaultResourceLoader();
			Resource storeFile = loader.getResource("classpath:/saml/samlKeystore.jks");
			String storePass = "nalle123";
			Map<String, String> passwords = new HashMap<String, String>();
			passwords.put("apollo", "nalle123");
			String defaultKey = "apollo";
			keyManager = new JKSKeyManager(storeFile, storePass, passwords, defaultKey);
		}
		return keyManager;
	}

}
