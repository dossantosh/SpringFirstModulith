package com.dossantosh.springfirstmodulith.users.domain.ports;

import com.dossantosh.springfirstmodulith.users.domain.Modules;
import com.dossantosh.springfirstmodulith.users.domain.Roles;
import com.dossantosh.springfirstmodulith.users.domain.Submodules;

import java.util.Optional;

public interface AccessCatalog {

	Optional<Roles> findRoleByName(String roleName);

	Optional<Modules> findModuleByName(String moduleName);

	Optional<Submodules> findSubmoduleByModuleAndName(String moduleName, String submoduleName);
}
