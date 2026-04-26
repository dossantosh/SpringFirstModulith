package com.dossantosh.springfirstmodulith.security;

import com.dossantosh.springfirstmodulith.authorization.AuthorizationScopes;
import com.dossantosh.springfirstmodulith.security.api.AuthCapabilitiesResponse;
import com.dossantosh.springfirstmodulith.security.api.AuthCapabilitiesMapper;
import com.dossantosh.springfirstmodulith.security.login.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
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

		return authentication.getAuthorities().stream().map(granted -> granted.getAuthority())
				.filter(authority -> authority.startsWith(SecurityAuthorityNames.ROLE_PREFIX))
				.map(authority -> authority.substring(SecurityAuthorityNames.ROLE_PREFIX.length())).sorted().toList();
	}

	public List<String> effectiveScopes(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			return List.of();
		}
		if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
			return userDetails.getScopes();
		}

		return authentication.getAuthorities().stream().map(granted -> granted.getAuthority())
				.filter(AuthorizationScopes::isScopeAuthority).sorted().toList();
	}

	public AuthCapabilitiesResponse capabilities(Authentication authentication) {
		return AuthCapabilitiesMapper.fromScopes(effectiveScopes(authentication));
	}

	public List<String> mergeScopes(Collection<String> roleScopes, Collection<String> directScopes) {
		ArrayList<String> scopes = new ArrayList<>();
		if (roleScopes != null) {
			scopes.addAll(roleScopes);
		}
		if (directScopes != null) {
			scopes.addAll(directScopes);
		}
		return scopes.stream().filter(AuthorizationScopes::isScopeAuthority).distinct().sorted().toList();
	}
}
