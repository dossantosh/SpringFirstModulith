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

		boolean canReadUsers = current.contains(AuthorizationScopes.USER_READ);
		boolean canReadPerfumes = current.contains(AuthorizationScopes.PERFUME_READ);

		return new AuthCapabilitiesResponse(
				new FeatureCapabilityResponse(canReadUsers || canCreateUsers || canUpdateUsers || canDeleteUsers,
						canReadUsers, canCreateUsers, canUpdateUsers, canDeleteUsers),
				new FeatureCapabilityResponse(
						canReadPerfumes || canCreatePerfumes || canUpdatePerfumes || canDeletePerfumes, canReadPerfumes,
						canCreatePerfumes, canUpdatePerfumes, canDeletePerfumes));
	}
}
