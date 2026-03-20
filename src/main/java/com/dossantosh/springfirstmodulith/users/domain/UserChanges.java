package com.dossantosh.springfirstmodulith.users.domain;

public record UserChanges(String username, String email, Boolean enabled, String password, Boolean isAdmin,
		UserAccess access) {
}
