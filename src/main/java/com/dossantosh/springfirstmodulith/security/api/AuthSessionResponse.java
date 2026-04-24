package com.dossantosh.springfirstmodulith.security.api;

import java.util.List;

record AuthSessionResponse(String username, List<String> authorities, String dataSource,
		AuthCapabilitiesResponse capabilities) {
}
