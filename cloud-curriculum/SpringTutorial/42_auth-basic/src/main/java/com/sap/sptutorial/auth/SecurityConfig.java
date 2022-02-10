package com.sap.sptutorial.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> configurer = auth.inMemoryAuthentication();
		configurer.withUser("user").password("user").roles("NOBODY", "USER");
		configurer.withUser("admin").password("admin").roles("NOBODY", "USER", "ADMIN");
		configurer.withUser("nobody").password("nobody").roles("NOBODY");
	}

	protected void configure(HttpSecurity http) throws Exception {
		http./* formLogin() */httpBasic().and().csrf().disable().authorizeRequests()
				.antMatchers("/deal/secret*").hasRole("ADMIN").antMatchers("/deal").hasRole("USER").antMatchers("/**").authenticated();
	}
}
