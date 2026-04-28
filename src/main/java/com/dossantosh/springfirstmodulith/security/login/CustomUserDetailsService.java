package com.dossantosh.springfirstmodulith.security.login;

import com.dossantosh.springfirstmodulith.users.api.ports.login.UserAuthQuery;
import com.dossantosh.springfirstmodulith.users.api.ports.login.UserAuthView;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomUserDetailsService implements UserDetailsService {

	private final UserAuthQuery userAuthQuery;

	CustomUserDetailsService(UserAuthQuery userAuthQuery) {
		this.userAuthQuery = userAuthQuery;
	}

	@Override
	public UserDetails loadUserByUsername(String username) {
		UserAuthView user = userAuthQuery.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(username));

		List<SimpleGrantedAuthority> authorities = user.scopes().stream().map(SimpleGrantedAuthority::new).toList();

		CustomUserDetails userDetails = new CustomUserDetails();
		userDetails.setId(user.id());
		userDetails.setUsername(user.username());
		userDetails.setEmail(user.email());
		userDetails.setPassword(user.password());
		userDetails.setEnabled(user.enabled());
		userDetails.setIsAdmin(user.isAdmin());
		userDetails.setRoles(user.roles());
		userDetails.setScopes(user.scopes());
		userDetails.setAuthorities(List.copyOf(authorities));

		return userDetails;
	}
}
