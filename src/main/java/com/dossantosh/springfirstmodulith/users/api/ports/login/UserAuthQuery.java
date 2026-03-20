package com.dossantosh.springfirstmodulith.users.api.ports.login;

import java.util.Optional;

public interface UserAuthQuery {

	Optional<UserAuthView> findByUsername(String username);
}
