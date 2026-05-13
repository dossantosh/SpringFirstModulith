package com.dossantosh.springfirstmodulith.users.domain.ports;

import com.dossantosh.springfirstmodulith.users.domain.entities.Roles;

import java.util.Optional;

public interface AccessCatalog {

	Optional<Roles> findRoleByName(String roleName);
}
