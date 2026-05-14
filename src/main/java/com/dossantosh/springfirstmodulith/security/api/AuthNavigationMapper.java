package com.dossantosh.springfirstmodulith.security.api;

import java.util.List;

import com.dossantosh.springfirstmodulith.navigation.api.ports.NavigationItemView;
import com.dossantosh.springfirstmodulith.navigation.api.ports.NavigationModuleView;

final class AuthNavigationMapper {

	private AuthNavigationMapper() {
	}

	static List<NavigationModuleResponse> fromCatalog(List<NavigationModuleView> modules) {
		if (modules == null || modules.isEmpty()) {
			return List.of();
		}

		return modules.stream().map(AuthNavigationMapper::toResponse).toList();
	}

	private static NavigationModuleResponse toResponse(NavigationModuleView module) {
		return new NavigationModuleResponse(module.key(), module.label(), module.icon(),
				module.items().stream().map(AuthNavigationMapper::toResponse).toList());
	}

	private static NavigationItemResponse toResponse(NavigationItemView item) {
		return new NavigationItemResponse(item.key(), item.label(), item.icon(), item.route(), item.disabled(),
				item.hint());
	}
}
