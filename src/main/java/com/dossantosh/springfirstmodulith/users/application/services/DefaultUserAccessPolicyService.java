package com.dossantosh.springfirstmodulith.users.application.services;

import com.dossantosh.springfirstmodulith.core.exceptions.custom.BusinessException;
import com.dossantosh.springfirstmodulith.users.domain.Roles;
import com.dossantosh.springfirstmodulith.users.domain.UserAccess;
import com.dossantosh.springfirstmodulith.users.domain.ports.AccessCatalog;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class DefaultUserAccessPolicyService {

	private static final String DEFAULT_ROLE_SYSTEMS = "SYSTEMS";

	private final AccessCatalog accessCatalog;

	public DefaultUserAccessPolicyService(AccessCatalog accessCatalog) {
		this.accessCatalog = accessCatalog;
	}

	public UserAccess defaultAccessForNewUser() {
		Roles userRole = accessCatalog.findRoleByName(DEFAULT_ROLE_SYSTEMS)
				.orElseThrow(() -> new BusinessException("Default role '" + DEFAULT_ROLE_SYSTEMS + "' was not found"));

		return UserAccess.of(Set.of(userRole));
	}
}
