package com.dossantosh.springfirstmodulith.authorization;

import java.util.Set;

public final class AuthorizationScopes {

	public static final String USER_READ = "users:read";
	public static final String USER_CREATE = "users:create";
	public static final String USER_UPDATE = "users:update";
	public static final String USER_DELETE = "users:delete";

	public static final String PERFUME_READ = "perfumes:read";
	public static final String PERFUME_CREATE = "perfumes:create";
	public static final String PERFUME_UPDATE = "perfumes:update";
	public static final String PERFUME_DELETE = "perfumes:delete";

	public static final String ROLE_READ = "role:read";
	public static final String ROLE_ASSIGN = "role:assign";

	public static final String SCOPE_READ = "scope:read";
	public static final String SCOPE_ASSIGN = "scope:assign";

	public static final Set<String> ALL = Set.of(USER_READ, USER_CREATE, USER_UPDATE, USER_DELETE, PERFUME_READ,
			PERFUME_CREATE, PERFUME_UPDATE, PERFUME_DELETE, ROLE_READ, ROLE_ASSIGN, SCOPE_READ, SCOPE_ASSIGN);

	private AuthorizationScopes() {
	}

	public static boolean isScopeAuthority(String authority) {
		return authority != null && authority.contains(":");
	}
}
