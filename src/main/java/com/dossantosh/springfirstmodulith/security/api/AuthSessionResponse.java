package com.dossantosh.springfirstmodulith.security.api;

import java.util.List;

record AuthSessionResponse(Long userId, String username, String dataSource, List<String> roles, List<String> scopes,
		AuthCapabilitiesResponse capabilities) {
}
