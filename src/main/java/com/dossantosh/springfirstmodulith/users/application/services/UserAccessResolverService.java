package com.dossantosh.springfirstmodulith.users.application.services;

import com.dossantosh.springfirstmodulith.core.exceptions.custom.BusinessException;
import com.dossantosh.springfirstmodulith.users.application.ports.out.UserAccessLookupPort;
import com.dossantosh.springfirstmodulith.users.domain.Modules;
import com.dossantosh.springfirstmodulith.users.domain.Roles;
import com.dossantosh.springfirstmodulith.users.domain.Submodules;
import com.dossantosh.springfirstmodulith.users.domain.UserAccess;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserAccessResolverService {

	private final UserAccessLookupPort userAccessLookupPort;

	public UserAccessResolverService(UserAccessLookupPort userAccessLookupPort) {
		this.userAccessLookupPort = userAccessLookupPort;
	}

	public UserAccess resolve(List<Long> roleIds, List<Long> moduleIds, List<Long> submoduleIds) {
		Set<Long> distinctRoleIds = toDistinctIds(roleIds, "roleIds");
		Set<Long> distinctModuleIds = toDistinctIds(moduleIds, "moduleIds");
		Set<Long> distinctSubmoduleIds = toDistinctIds(submoduleIds, "submoduleIds");

		Set<Roles> roles = new LinkedHashSet<>(userAccessLookupPort.findRolesById(List.copyOf(distinctRoleIds)));
		Set<Modules> modules = new LinkedHashSet<>(
				userAccessLookupPort.findModulesById(List.copyOf(distinctModuleIds)));
		Set<Submodules> submodules = new LinkedHashSet<>(
				userAccessLookupPort.findSubmodulesById(List.copyOf(distinctSubmoduleIds)));

		if (roles.size() != distinctRoleIds.size()) {
			throw new BusinessException("One or more roles were not found");
		}
		if (modules.size() != distinctModuleIds.size()) {
			throw new BusinessException("One or more modules were not found");
		}
		if (submodules.size() != distinctSubmoduleIds.size()) {
			throw new BusinessException("One or more submodules were not found");
		}

		return UserAccess.of(roles, modules, submodules);
	}

	private Set<Long> toDistinctIds(List<Long> ids, String fieldName) {
		if (ids == null || ids.isEmpty()) {
			throw new BusinessException(fieldName + " cannot be empty");
		}

		LinkedHashSet<Long> normalized = new LinkedHashSet<>();
		for (Long id : ids) {
			if (id == null || id <= 0) {
				throw new BusinessException(fieldName + " contains an invalid id");
			}
			normalized.add(id);
		}
		return normalized;
	}
}
