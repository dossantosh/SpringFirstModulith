package com.dossantosh.springfirstmodulith.security.login;

import com.dossantosh.springfirstmodulith.users.api.ports.login.UserAuthView;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class CustomUserDetails implements UserDetails {

	private final Long id;
	private final String username;
	private final String email;
	private final String password;
	private final boolean enabled;
	private final boolean isAdmin;
	private final List<String> roles;
	private final List<String> scopes;

	private final List<GrantedAuthority> authorities;

	private CustomUserDetails(Long id, String username, String email, String password, boolean enabled, boolean isAdmin,
			List<String> roles, List<String> scopes, List<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.username = Objects.requireNonNull(username, "username cannot be null");
		this.email = email;
		this.password = Objects.requireNonNull(password, "password cannot be null");
		this.enabled = enabled;
		this.isAdmin = isAdmin;
		this.roles = roles == null ? List.of() : List.copyOf(roles);
		this.scopes = scopes == null ? List.of() : List.copyOf(scopes);
		this.authorities = authorities == null ? List.of() : List.copyOf(authorities);
	}

	public static CustomUserDetails from(UserAuthView user) {
		Objects.requireNonNull(user, "user cannot be null");

		List<String> scopes = user.scopes() == null ? List.of() : List.copyOf(user.scopes());
		List<SimpleGrantedAuthority> authorities = scopes.stream().map(SimpleGrantedAuthority::new).toList();

		return new CustomUserDetails(user.id(), user.username(), user.email(), user.password(), user.enabled(),
				user.isAdmin(), user.roles(), scopes, authorities);
	}

	public Long getId() {
		return id;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public Boolean getEnabledValue() {
		return enabled;
	}

	public Boolean getIsAdmin() {
		return isAdmin;
	}

	public List<String> getRoles() {
		return roles;
	}

	public List<String> getScopes() {
		return scopes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
}
