package com.dossantosh.springfirstmodulith.security;

public final class SecurityAuthorityNames {

	public static final String ROLE_PREFIX = "ROLE_";
	public static final String MODULE_PREFIX = "MODULE_";
	public static final String SUBMODULE_PREFIX = "SUBMODULE_";

	public static final String ROLE_USER = ROLE_PREFIX + "USER";
	public static final String MODULE_USERS = MODULE_PREFIX + "USERS";
	public static final String MODULE_PERFUMES = MODULE_PREFIX + "PERFUMES";
	public static final String SUBMODULE_READ_USERS = SUBMODULE_PREFIX + "READUSERS";
	public static final String SUBMODULE_WRITE_USERS = SUBMODULE_PREFIX + "WRITEUSERS";
	public static final String SUBMODULE_READ_PERFUMES = SUBMODULE_PREFIX + "READPERFUMES";
	public static final String SUBMODULE_WRITE_PERFUMES = SUBMODULE_PREFIX + "WRITEPERFUMES";

	private SecurityAuthorityNames() {
	}

	public static String role(String roleName) {
		return ROLE_PREFIX + normalize(roleName);
	}

	public static String module(String moduleName) {
		return MODULE_PREFIX + normalize(moduleName);
	}

	public static String submodule(String submoduleName) {
		return SUBMODULE_PREFIX + normalize(submoduleName);
	}

	private static String normalize(String value) {
		return value == null ? "" : value.trim().toUpperCase().replace(' ', '_');
	}
}
