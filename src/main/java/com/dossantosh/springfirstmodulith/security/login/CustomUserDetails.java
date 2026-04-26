package com.dossantosh.springfirstmodulith.security.login;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

	private Long id;
	private String username;
	private String email;
	private String password;
	private Boolean enabled;
	private Boolean isAdmin;
	private List<String> roles;
	private List<String> scopes;

	private List<GrantedAuthority> authorities;

	public CustomUserDetails() {
	}

	public CustomUserDetails(Long id, String username, String email, String password, Boolean enabled, Boolean isAdmin,
			List<GrantedAuthority> authorities) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.enabled = enabled;
		this.isAdmin = isAdmin;
		this.authorities = authorities;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getEnabledValue() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public List<String> getRoles() {
		return roles == null ? List.of() : roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles == null ? List.of() : List.copyOf(roles);
	}

	public List<String> getScopes() {
		return scopes == null ? List.of() : scopes;
	}

	public void setScopes(List<String> scopes) {
		this.scopes = scopes == null ? List.of() : List.copyOf(scopes);
	}

	public void setAuthorities(List<GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities == null ? List.of() : authorities;
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
		return Boolean.TRUE.equals(enabled);
	}
}
