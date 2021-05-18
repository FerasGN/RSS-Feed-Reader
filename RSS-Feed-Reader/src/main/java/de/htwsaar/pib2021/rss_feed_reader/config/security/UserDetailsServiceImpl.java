package de.htwsaar.pib2021.rss_feed_reader.config.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.UserRepository;

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