package com.dossantosh.springfirstmodulith.users.infrastructure.adapters;

import com.dossantosh.springfirstmodulith.users.application.ports.out.UserAccessLookupPort;
import com.dossantosh.springfirstmodulith.users.domain.Modules;
import com.dossantosh.springfirstmodulith.users.domain.Roles;
import com.dossantosh.springfirstmodulith.users.domain.Submodules;
import com.dossantosh.springfirstmodulith.users.domain.ports.AccessCatalog;
import com.dossantosh.springfirstmodulith.users.infrastructure.repos.ModuleRepository;
import com.dossantosh.springfirstmodulith.users.infrastructure.repos.RoleRepository;
import com.dossantosh.springfirstmodulith.users.infrastructure.repos.SubmoduleRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class JpaAccessAdapter implements UserAccessLookupPort, AccessCatalog {

	private final RoleRepository roleRepository;
	private final ModuleRepository moduleRepository;
	private final SubmoduleRepository submoduleRepository;

	JpaAccessAdapter(RoleRepository roleRepository, ModuleRepository moduleRepository,
			SubmoduleRepository submoduleRepository) {
		this.roleRepository = roleRepository;
		this.moduleRepository = moduleRepository;
		this.submoduleRepository = submoduleRepository;
	}

	@Override
	public List<Roles> findRolesById(List<Long> ids) {
		return roleRepository.findAllById(ids);
	}

	@Override
	public List<Modules> findModulesById(List<Long> ids) {
		return moduleRepository.findAllById(ids);
	}

	@Override
	public List<Submodules> findSubmodulesById(List<Long> ids) {
		return submoduleRepository.findAllById(ids);
	}

	@Override
	public Optional<Roles> findRoleByName(String roleName) {
		return roleRepository.findByNameIgnoreCase(roleName);
	}

	@Override
	public Optional<Modules> findModuleByName(String moduleName) {
		return moduleRepository.findByNameIgnoreCase(moduleName);
	}

	@Override
	public Optional<Submodules> findSubmoduleByModuleAndName(String moduleName, String submoduleName) {
		return submoduleRepository.findByModuleNameAndSubmoduleName(moduleName, submoduleName);
	}
}
