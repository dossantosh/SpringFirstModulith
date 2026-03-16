package com.dossantosh.springfirstmodulith.users.application.services;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.dossantosh.springfirstmodulith.core.errors.custom.BusinessException;
import com.dossantosh.springfirstmodulith.users.domain.Modules;
import com.dossantosh.springfirstmodulith.users.domain.Roles;
import com.dossantosh.springfirstmodulith.users.domain.Submodules;
import com.dossantosh.springfirstmodulith.users.domain.UserAccess;
import com.dossantosh.springfirstmodulith.users.domain.ports.AccessCatalog;

@Service
public class DefaultUserAccessPolicyService {

    private final AccessCatalog accessCatalog;

    public DefaultUserAccessPolicyService(AccessCatalog accessCatalog) {
        this.accessCatalog = accessCatalog;
    }

    public UserAccess defaultAccessForNewUser() {
        Roles userRole = accessCatalog.findRoleByName("USER")
                .orElseThrow(() -> new BusinessException("Default role 'USER' was not found"));

        Modules usersModule = accessCatalog.findModuleByName("Users")
                .orElseThrow(() -> new BusinessException("Default module 'Users' was not found"));

        Submodules readUsers = accessCatalog.findSubmoduleByModuleAndName("Users", "ReadUsers")
                .orElseThrow(() -> new BusinessException("Default submodule 'Users/ReadUsers' was not found"));

        return UserAccess.of(Set.of(userRole), Set.of(usersModule), Set.of(readUsers));
    }
}
