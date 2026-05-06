package com.dossantosh.springfirstmodulith.authorization;

import java.util.Set;

public final class AuthorizationScopes {

	public static final String SYSTEMS_READ = "systems:read";
	public static final String SYSTEMS_WRITE = "systems:write";

	public static final String PERFUMES_READ = "perfumes:read";
	public static final String PERFUMES_WRITE = "perfumes:write";

	public static final Set<String> ALL = Set.of(SYSTEMS_READ, SYSTEMS_WRITE, PERFUMES_READ, PERFUMES_WRITE);

	private AuthorizationScopes() {
	}

	public static boolean isScopeAuthority(String authority) {
		return authority != null && authority.contains(":");
	}
}
