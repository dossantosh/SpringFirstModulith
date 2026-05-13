package com.dossantosh.springfirstmodulith.users.application.services;

import com.dossantosh.springfirstmodulith.core.exceptions.custom.BusinessException;
import com.dossantosh.springfirstmodulith.users.application.ports.out.RoleRepository;
import com.dossantosh.springfirstmodulith.users.domain.entities.Roles;
import com.dossantosh.springfirstmodulith.users.domain.UserAccess;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class DefaultUserAccessPolicyService {

	private static final String DEFAULT_ROLE_SYSTEMS = "SYSTEMS";

	private final RoleRepository roleRepository;

	public DefaultUserAccessPolicyService(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	public UserAccess defaultAccessForNewUser() {
		Roles userRole = roleRepository.findRoleByName(DEFAULT_ROLE_SYSTEMS)
				.orElseThrow(() -> new BusinessException("Default role '" + DEFAULT_ROLE_SYSTEMS + "' was not found"));

		return UserAccess.of(Set.of(userRole));
	}
}
