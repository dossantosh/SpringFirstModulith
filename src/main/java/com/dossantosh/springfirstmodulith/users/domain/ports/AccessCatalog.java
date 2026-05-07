package com.dossantosh.springfirstmodulith.users.domain.ports;

import com.dossantosh.springfirstmodulith.users.domain.Roles;

import java.util.Optional;

public interface AccessCatalog {

	Optional<Roles> findRoleByName(String roleName);
}
