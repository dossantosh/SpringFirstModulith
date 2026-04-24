package com.dossantosh.springfirstmodulith.security.api;

record AuthSessionResponse(String username, String dataSource, AuthCapabilitiesResponse capabilities) {
}
