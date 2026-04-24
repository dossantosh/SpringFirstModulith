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

		return new AuthCapabilitiesResponse(
				new FeatureCapabilityResponse(current.contains(SecurityAuthorityNames.MODULE_USERS),
						current.contains(SecurityAuthorityNames.SUBMODULE_READ_USERS),
						current.contains(SecurityAuthorityNames.SUBMODULE_WRITE_USERS)),
				new FeatureCapabilityResponse(current.contains(SecurityAuthorityNames.MODULE_PERFUMES),
						current.contains(SecurityAuthorityNames.SUBMODULE_READ_PERFUMES),
						current.contains(SecurityAuthorityNames.SUBMODULE_WRITE_PERFUMES)));
	}
}
