package com.dossantosh.springfirstmodulith.users.application.ports.out;

import com.dossantosh.springfirstmodulith.users.domain.entities.Roles;
import com.dossantosh.springfirstmodulith.users.domain.ports.AccessCatalog;

import java.util.List;

public interface RoleRepository extends AccessCatalog {

	List<Roles> findRolesById(List<Long> ids);

	List<Roles> findAllRoles();
}
