package com.dossantosh.springfirstmodulith.security.login;

import com.dossantosh.springfirstmodulith.users.api.ports.login.UserAuthQuery;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {

	private final UserAuthQuery userAuthQuery;

	CustomUserDetailsService(UserAuthQuery userAuthQuery) {
		this.userAuthQuery = userAuthQuery;
	}

	@Override
	public UserDetails loadUserByUsername(String username) {
		return userAuthQuery.findByUsername(username)
				.map(CustomUserDetails::from)
				.orElseThrow(() -> new UsernameNotFoundException(username));
	}
}
