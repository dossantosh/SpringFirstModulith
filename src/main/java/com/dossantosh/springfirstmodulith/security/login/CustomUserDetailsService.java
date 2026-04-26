package com.dossantosh.springfirstmodulith.security.login;

import com.dossantosh.springfirstmodulith.security.SecurityAuthorityNames;
import com.dossantosh.springfirstmodulith.users.api.ports.login.UserAuthQuery;
import com.dossantosh.springfirstmodulith.users.api.ports.login.UserAuthView;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

		var authorities = new ArrayList<SimpleGrantedAuthority>();

		for (String role : user.roles()) {
			authorities.add(new SimpleGrantedAuthority(SecurityAuthorityNames.role(role)));
		}

		for (String module : user.modules()) {
			authorities.add(new SimpleGrantedAuthority(SecurityAuthorityNames.module(module)));
		}

		for (String sub : user.submodules()) {
			authorities.add(new SimpleGrantedAuthority(SecurityAuthorityNames.submodule(sub)));
		}

		for (String scope : user.scopes()) {
			authorities.add(new SimpleGrantedAuthority(scope));
		}

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
