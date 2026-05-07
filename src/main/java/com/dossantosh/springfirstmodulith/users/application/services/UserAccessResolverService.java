package com.dossantosh.springfirstmodulith.users.application.services;

import com.dossantosh.springfirstmodulith.core.exceptions.custom.BusinessException;
import com.dossantosh.springfirstmodulith.users.application.ports.out.UserAccessLookupPort;
import com.dossantosh.springfirstmodulith.users.domain.Roles;
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

	public UserAccess resolve(List<Long> roleIds) {
		Set<Long> distinctRoleIds = toRequiredDistinctIds(roleIds, "roleIds");

		Set<Roles> roles = new LinkedHashSet<>(userAccessLookupPort.findRolesById(List.copyOf(distinctRoleIds)));

		if (roles.size() != distinctRoleIds.size()) {
			throw new BusinessException("One or more roles were not found");
		}

		return UserAccess.of(roles);
	}

	private Set<Long> toRequiredDistinctIds(List<Long> ids, String fieldName) {
		if (ids == null || ids.isEmpty()) {
			throw new BusinessException(fieldName + " cannot be empty");
		}

		return toOptionalDistinctIds(ids, fieldName);
	}

	private Set<Long> toOptionalDistinctIds(List<Long> ids, String fieldName) {
		if (ids == null || ids.isEmpty()) {
			return Set.of();
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
