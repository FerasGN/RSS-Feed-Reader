package com.feeedify.config.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.feeedify.database.entity.User;
import com.feeedify.database.repository.UserRepository;

public class UserDetailsServiceImpl implements UserDetailsService {

	private final static String USERNAME_NOT_FOUND = "Could not find User with the user name: ";

	@Autowired
	private UserRepository userRepositroy;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> user = userRepositroy.findByUsername(username.toLowerCase());
		if (!user.isPresent()) {
			throw new UsernameNotFoundException(USERNAME_NOT_FOUND + username);
		}
		return new SecurityUser(user.get());
	}

}