package com.dossantosh.springfirstmodulith.users.domain;

import com.dossantosh.springfirstmodulith.core.exceptions.custom.BusinessException;
import com.dossantosh.springfirstmodulith.users.domain.entities.Roles;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public record UserAccess(Set<Roles> roles) {

	public UserAccess {
		roles = toUnmodifiableCopy(roles);

		if (roles.isEmpty()) {
			throw new BusinessException("A user must have at least one role");
		}
	}

	public static UserAccess of(Set<Roles> roles) {
		return new UserAccess(roles);
	}

	private static <T> Set<T> toUnmodifiableCopy(Set<T> values) {
		if (values == null) {
			throw new BusinessException("roles" + " cannot be null");
		}

		LinkedHashSet<T> copy = new LinkedHashSet<>();
		for (T value : values) {
			if (value == null) {
				throw new BusinessException("roles" + " cannot contain null values");
			}
			copy.add(value);
		}
		return Collections.unmodifiableSet(copy);
	}
}
