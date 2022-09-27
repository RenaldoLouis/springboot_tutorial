package com.maul.app.ws.security;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.maul.app.ws.service.UserService;

@EnableWebSecurity
public class WebSecurity {
	private final UserService userDetailsService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public WebSecurity(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userDetailsService = userService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@Bean
	protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
		// Configure AuthenticationManagerBuilder
		AuthenticationManagerBuilder authenticationManagerBuilder = http
				.getSharedObject(AuthenticationManagerBuilder.class);

		authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);

		AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
		http.authenticationManager(authenticationManager);
		// Configure to allow API createUser accescible for all User even the one that
		// are not authenthicated
		http.csrf().disable().authorizeRequests().antMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL)
				.permitAll().anyRequest().authenticated().and()
				.addFilter(new AuthenticationFilter(authenticationManager));

		return http.build();
	}

	// OLD WAY WITH WebSecurityConfigurerAdapter
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		http.csrf().disable().authorizeRequests().antMatchers(HttpMethod.POST, "/maul/createUser").permitAll()
//				.anyRequest().authenticated();
//	}

//	@Override
//	public void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
//	}
}
