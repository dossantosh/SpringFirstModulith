package com.dossantosh.springfirstmodulith.security.api;

import com.dossantosh.springfirstmodulith.authorization.AuthorizationScopes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class AuthNavigationMapper {

	private AuthNavigationMapper() {
	}

	static List<NavigationModuleResponse> fromScopes(Collection<String> scopes) {
		Set<String> current = scopes == null ? Set.of() : new HashSet<>(scopes);
		List<NavigationModuleResponse> navigation = new ArrayList<>();

		List<NavigationItemResponse> systemItems = new ArrayList<>();
		if (current.contains(AuthorizationScopes.USER_READ)) {
			systemItems.add(new NavigationItemResponse("users_search", "Usuarios", "group", "/users/search", false,
					null));
		}
		if (!systemItems.isEmpty()) {
			navigation.add(new NavigationModuleResponse("systems", "Sistemas", "settings", List.copyOf(systemItems)));
		}

		List<NavigationItemResponse> perfumeItems = new ArrayList<>();
		if (current.contains(AuthorizationScopes.PERFUME_READ)) {
			perfumeItems.add(new NavigationItemResponse("perfumes_catalog", "Catalogo", "local_florist",
					"/perfumes/catalog", true, "Modulo previsto para proximas fases"));
		}
		if (!perfumeItems.isEmpty()) {
			navigation.add(new NavigationModuleResponse("perfumes", "Perfumes", "local_florist",
					List.copyOf(perfumeItems)));
		}

		return List.copyOf(navigation);
	}
}
