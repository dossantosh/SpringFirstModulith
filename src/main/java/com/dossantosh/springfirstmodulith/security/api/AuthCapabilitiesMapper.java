package com.dossantosh.springfirstmodulith.security.api;

import com.dossantosh.springfirstmodulith.security.SecurityAuthorityNames;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

final class AuthCapabilitiesMapper {

	private AuthCapabilitiesMapper() {
	}

	static AuthCapabilitiesResponse fromAuthorities(Collection<String> authorities) {
		Set<String> current = new HashSet<>(authorities);
		boolean canAccessUsers = current.contains(SecurityAuthorityNames.MODULE_USERS);
		boolean canAccessPerfumes = current.contains(SecurityAuthorityNames.MODULE_PERFUMES);

		return new AuthCapabilitiesResponse(
				new FeatureCapabilityResponse(canAccessUsers,
						canAccessUsers && current.contains(SecurityAuthorityNames.SUBMODULE_READ_USERS),
						canAccessUsers && current.contains(SecurityAuthorityNames.SUBMODULE_WRITE_USERS)),
				new FeatureCapabilityResponse(canAccessPerfumes,
						canAccessPerfumes && current.contains(SecurityAuthorityNames.SUBMODULE_READ_PERFUMES),
						canAccessPerfumes && current.contains(SecurityAuthorityNames.SUBMODULE_WRITE_PERFUMES)));
	}
}
