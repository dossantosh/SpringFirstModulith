package com.dossantosh.springfirstmodulith.security;

import com.dossantosh.springfirstmodulith.authorization.AuthorizationScopes;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("permissions")
public class Permissions {

	private final AuthorizationService authorizationService;

	public Permissions(AuthorizationService authorizationService) {
		this.authorizationService = authorizationService;
	}

	public boolean canAccessUsers(Authentication authentication) {
		return canReadUsers(authentication) || canWriteUsers(authentication);
	}

	public boolean canReadUsers(Authentication authentication) {
		return hasScope(authentication, AuthorizationScopes.USER_READ);
	}

	public boolean canWriteUsers(Authentication authentication) {
		return hasScope(authentication, AuthorizationScopes.USER_CREATE)
				|| hasScope(authentication, AuthorizationScopes.USER_UPDATE)
				|| hasScope(authentication, AuthorizationScopes.USER_DELETE);
	}

	public boolean hasScope(Authentication authentication, String scope) {
		return authorizationService.hasScope(authentication, scope);
	}
}
