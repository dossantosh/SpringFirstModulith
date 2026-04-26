package com.dossantosh.springfirstmodulith.authorization;

import java.util.Set;

public final class AuthorizationScopes {

	public static final String USER_READ = "user:read";
	public static final String USER_CREATE = "user:create";
	public static final String USER_UPDATE = "user:update";
	public static final String USER_DELETE = "user:delete";

	public static final String PERFUME_READ = "perfume:read";
	public static final String PERFUME_CREATE = "perfume:create";
	public static final String PERFUME_UPDATE = "perfume:update";
	public static final String PERFUME_DELETE = "perfume:delete";

	public static final Set<String> ALL = Set.of(USER_READ, USER_CREATE, USER_UPDATE, USER_DELETE, PERFUME_READ,
			PERFUME_CREATE, PERFUME_UPDATE, PERFUME_DELETE);

	private AuthorizationScopes() {
	}

	public static boolean isScopeAuthority(String authority) {
		return authority != null && authority.contains(":") && !authority.startsWith("ROLE_")
				&& !authority.startsWith("MODULE_") && !authority.startsWith("SUBMODULE_");
	}
}
