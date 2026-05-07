package com.dossantosh.springfirstmodulith.security;

import com.dossantosh.springfirstmodulith.authorization.AuthorizationScopes;
import com.dossantosh.springfirstmodulith.security.login.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorizationService {

	public boolean hasScope(Authentication authentication, String scope) {
		if (authentication == null || !authentication.isAuthenticated()) {
			return false;
		}
		return effectiveScopes(authentication).contains(scope);
	}

	public List<String> roles(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			return List.of();
		}
		if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
			return userDetails.getRoles();
		}
		return List.of();
	}

	public List<String> effectiveScopes(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			return List.of();
		}
		if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
			return userDetails.getScopes();
		}

		return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.filter(AuthorizationScopes::isScopeAuthority).sorted().toList();
	}
}
