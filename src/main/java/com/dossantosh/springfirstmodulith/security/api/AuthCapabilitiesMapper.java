package com.dossantosh.springfirstmodulith.security.api;

import com.dossantosh.springfirstmodulith.authorization.AuthorizationScopes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class AuthCapabilitiesMapper {

	private AuthCapabilitiesMapper() {
	}

	public static AuthCapabilitiesResponse fromScopes(Collection<String> scopes) {
		Set<String> current = scopes == null ? Set.of() : new HashSet<>(scopes);
		boolean canCreateUsers = current.contains(AuthorizationScopes.USER_CREATE);
		boolean canUpdateUsers = current.contains(AuthorizationScopes.USER_UPDATE);
		boolean canDeleteUsers = current.contains(AuthorizationScopes.USER_DELETE);
		boolean canCreatePerfumes = current.contains(AuthorizationScopes.PERFUME_CREATE);
		boolean canUpdatePerfumes = current.contains(AuthorizationScopes.PERFUME_UPDATE);
		boolean canDeletePerfumes = current.contains(AuthorizationScopes.PERFUME_DELETE);

		return new AuthCapabilitiesResponse(
				new FeatureCapabilityResponse(current.contains(AuthorizationScopes.USER_READ),
						canCreateUsers || canUpdateUsers || canDeleteUsers, canCreateUsers, canUpdateUsers,
						canDeleteUsers),
				new FeatureCapabilityResponse(current.contains(AuthorizationScopes.PERFUME_READ),
						canCreatePerfumes || canUpdatePerfumes || canDeletePerfumes, canCreatePerfumes,
						canUpdatePerfumes, canDeletePerfumes));
	}
}
