package com.dossantosh.springfirstmodulith.security.api;

import java.util.List;

record CurrentUserCapabilitiesResponse(Long userId, String username, List<String> roles, List<String> scopes,
		AuthCapabilitiesResponse capabilities) {
}
