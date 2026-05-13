package com.dossantosh.springfirstmodulith.users.application.views;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public record UserRolesView(Long userId, String username, Set<RoleView> roles,
		List<RoleView> availableRoles) implements Serializable {
}
