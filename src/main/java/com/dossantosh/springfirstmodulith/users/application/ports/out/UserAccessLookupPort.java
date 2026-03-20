package com.dossantosh.springfirstmodulith.users.application.ports.out;

import com.dossantosh.springfirstmodulith.users.domain.Modules;
import com.dossantosh.springfirstmodulith.users.domain.Roles;
import com.dossantosh.springfirstmodulith.users.domain.Submodules;

import java.util.List;

public interface UserAccessLookupPort {

	List<Roles> findRolesById(List<Long> ids);

	List<Modules> findModulesById(List<Long> ids);

	List<Submodules> findSubmodulesById(List<Long> ids);
}
