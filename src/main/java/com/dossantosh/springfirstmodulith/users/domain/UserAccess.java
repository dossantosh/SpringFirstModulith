package com.dossantosh.springfirstmodulith.users.domain;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import com.dossantosh.springfirstmodulith.core.errors.custom.BusinessException;

public record UserAccess(Set<Roles> roles, Set<Modules> modules, Set<Submodules> submodules) {

    public UserAccess {
        roles = toUnmodifiableCopy(roles, "roles");
        modules = toUnmodifiableCopy(modules, "modules");
        submodules = toUnmodifiableCopy(submodules, "submodules");

        if (roles.isEmpty()) {
            throw new BusinessException("A user must have at least one role");
        }
        if (modules.isEmpty()) {
            throw new BusinessException("A user must have at least one module");
        }
        if (submodules.isEmpty()) {
            throw new BusinessException("A user must have at least one submodule");
        }

        validateEverySubmoduleBelongsToAnAssignedModule(modules, submodules);
    }

    public static UserAccess of(Set<Roles> roles, Set<Modules> modules, Set<Submodules> submodules) {
        return new UserAccess(roles, modules, submodules);
    }

    private static <T> Set<T> toUnmodifiableCopy(Set<T> values, String fieldName) {
        if (values == null) {
            throw new BusinessException(fieldName + " cannot be null");
        }

        LinkedHashSet<T> copy = new LinkedHashSet<>();
        for (T value : values) {
            if (value == null) {
                throw new BusinessException(fieldName + " cannot contain null values");
            }
            copy.add(value);
        }
        return Collections.unmodifiableSet(copy);
    }

    private static void validateEverySubmoduleBelongsToAnAssignedModule(Set<Modules> modules, Set<Submodules> submodules) {
        Set<Long> assignedModuleIds = modules.stream()
                .map(Modules::getId)
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.toSet());

        for (Submodules submodule : submodules) {
            Modules parentModule = submodule.getModule();
            if (parentModule == null || parentModule.getId() == null) {
                throw new BusinessException("Every submodule must belong to a persisted module");
            }
            if (!assignedModuleIds.contains(parentModule.getId())) {
                throw new BusinessException("Submodule '" + submodule.getName() + "' requires its parent module to be assigned");
            }
        }
    }
}
