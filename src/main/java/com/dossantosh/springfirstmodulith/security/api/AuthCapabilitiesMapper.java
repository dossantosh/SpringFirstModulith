package com.dossantosh.springfirstmodulith.security.api;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

final class AuthCapabilitiesMapper {

	private static final String MODULE_USERS = "MODULE_USERS";
	private static final String MODULE_PERFUMES = "MODULE_PERFUMES";
	private static final String SUBMODULE_READ_USERS = "SUBMODULE_READUSERS";
	private static final String SUBMODULE_WRITE_USERS = "SUBMODULE_WRITEUSERS";
	private static final String SUBMODULE_READ_PERFUMES = "SUBMODULE_READPERFUMES";
	private static final String SUBMODULE_WRITE_PERFUMES = "SUBMODULE_WRITEPERFUMES";

	private AuthCapabilitiesMapper() {
	}

	static AuthCapabilitiesResponse fromAuthorities(Collection<String> authorities) {
		Set<String> current = new HashSet<>(authorities);

		return new AuthCapabilitiesResponse(
				new FeatureCapabilityResponse(current.contains(MODULE_USERS), current.contains(SUBMODULE_READ_USERS),
						current.contains(SUBMODULE_WRITE_USERS)),
				new FeatureCapabilityResponse(current.contains(MODULE_PERFUMES),
						current.contains(SUBMODULE_READ_PERFUMES), current.contains(SUBMODULE_WRITE_PERFUMES)));
	}
}
