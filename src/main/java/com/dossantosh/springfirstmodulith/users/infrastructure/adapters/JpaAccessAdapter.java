package com.dossantosh.springfirstmodulith.users.infrastructure.adapters;

import com.dossantosh.springfirstmodulith.users.application.ports.out.UserAccessLookupPort;
import com.dossantosh.springfirstmodulith.users.domain.Roles;
import com.dossantosh.springfirstmodulith.users.domain.ports.AccessCatalog;
import com.dossantosh.springfirstmodulith.users.infrastructure.repos.RoleRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class JpaAccessAdapter implements UserAccessLookupPort, AccessCatalog {

	private final RoleRepository roleRepository;

	JpaAccessAdapter(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	@Override
	public List<Roles> findRolesById(List<Long> ids) {
		return roleRepository.findAllById(ids);
	}

	@Override
	public Optional<Roles> findRoleByName(String roleName) {
		return roleRepository.findByNameIgnoreCase(roleName);
	}
}
