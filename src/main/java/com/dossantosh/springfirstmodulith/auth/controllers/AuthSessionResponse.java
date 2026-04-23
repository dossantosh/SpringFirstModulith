package com.dossantosh.springfirstmodulith.auth.controllers;

import java.util.List;

record AuthSessionResponse(String username, List<String> authorities, String dataSource) {
}
