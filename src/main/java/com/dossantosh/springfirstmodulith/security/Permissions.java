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

	public boolean canApplyUserAccessChange(Authentication authentication, Object requestedAccess) {
		return requestedAccess == null || hasScope(authentication, AuthorizationScopes.SYSTEMS_WRITE);
	}

	public boolean hasScope(Authentication authentication, String scope) {
		return authorizationService.hasScope(authentication, scope);
	}
}
