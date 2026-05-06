package com.dossantosh.springfirstmodulith.security.api;

import java.util.List;

record AuthSessionResponse(Long userId, String username, String dataSource, List<String> roles, List<String> scopes,
		List<NavigationModuleResponse> navigation) {
}

record NavigationModuleResponse(String key, String label, String icon, List<NavigationItemResponse> items) {
}

record NavigationItemResponse(String key, String label, String icon, String route, boolean disabled, String hint) {
}
