package com.dossantosh.springfirstmodulith.users.application.views;

import java.io.Serializable;
import java.util.Set;

public record UserDetailsView(Long id, String username, String email, Boolean enabled, Boolean isAdmin,
		Set<RoleView> roles, Set<ModuleView> modules, Set<SubmoduleView> submodules) implements Serializable {
}
