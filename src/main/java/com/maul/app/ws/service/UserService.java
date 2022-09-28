package com.maul.app.ws.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.maul.app.ws.shared.dto.UserDto;

public interface UserService extends UserDetailsService {
	UserDto createUser(UserDto user); // yang dipaling kiri itu nentuin type balikannya apa

	UserDto getUser(String email);

	UserDto getUserByUserId(String userId);
}
