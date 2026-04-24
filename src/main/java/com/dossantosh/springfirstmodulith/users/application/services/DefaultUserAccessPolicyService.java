package com.dossantosh.springfirstmodulith.users.application.services;

import com.dossantosh.springfirstmodulith.core.exceptions.custom.BusinessException;
import com.dossantosh.springfirstmodulith.users.domain.Modules;
import com.dossantosh.springfirstmodulith.users.domain.Roles;
import com.dossantosh.springfirstmodulith.users.domain.Submodules;
import com.dossantosh.springfirstmodulith.users.domain.UserAccess;
import com.dossantosh.springfirstmodulith.users.domain.ports.AccessCatalog;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class DefaultUserAccessPolicyService {

	private static final String DEFAULT_ROLE_USER = "USER";
	private static final String USERS_MODULE = "USERS";
	private static final String READ_USERS_SUBMODULE = "READUSERS";

	private final AccessCatalog accessCatalog;

	public DefaultUserAccessPolicyService(AccessCatalog accessCatalog) {
		this.accessCatalog = accessCatalog;
	}

	public UserAccess defaultAccessForNewUser() {
		Roles userRole = accessCatalog.findRoleByName(DEFAULT_ROLE_USER)
				.orElseThrow(() -> new BusinessException("Default role '" + DEFAULT_ROLE_USER + "' was not found"));

		Modules usersModule = accessCatalog.findModuleByName(USERS_MODULE)
				.orElseThrow(() -> new BusinessException("Default module '" + USERS_MODULE + "' was not found"));

		Submodules readUsers = accessCatalog.findSubmoduleByModuleAndName(USERS_MODULE, READ_USERS_SUBMODULE)
				.orElseThrow(() -> new BusinessException(
						"Default submodule '" + USERS_MODULE + "/" + READ_USERS_SUBMODULE + "' was not found"));

		return UserAccess.of(Set.of(userRole), Set.of(usersModule), Set.of(readUsers));
	}
}
