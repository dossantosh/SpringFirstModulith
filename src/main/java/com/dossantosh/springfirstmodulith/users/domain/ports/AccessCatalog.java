package com.dossantosh.springfirstmodulith.users.domain.ports;

import java.util.Optional;

import com.dossantosh.springfirstmodulith.users.domain.Modules;
import com.dossantosh.springfirstmodulith.users.domain.Roles;
import com.dossantosh.springfirstmodulith.users.domain.Submodules;

public interface AccessCatalog {

    Optional<Roles> findRoleByName(String roleName);

    Optional<Modules> findModuleByName(String moduleName);

    Optional<Submodules> findSubmoduleByModuleAndName(String moduleName, String submoduleName);
}
