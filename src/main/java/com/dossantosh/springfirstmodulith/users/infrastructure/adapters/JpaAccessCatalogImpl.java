package com.dossantosh.springfirstmodulith.users.infrastructure.adapters;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.dossantosh.springfirstmodulith.users.domain.Modules;
import com.dossantosh.springfirstmodulith.users.domain.Roles;
import com.dossantosh.springfirstmodulith.users.domain.Submodules;
import com.dossantosh.springfirstmodulith.users.domain.ports.AccessCatalog;
import com.dossantosh.springfirstmodulith.users.infrastructure.mappers.AccessReferenceMapper;
import com.dossantosh.springfirstmodulith.users.infrastructure.repos.ModuleRepository;
import com.dossantosh.springfirstmodulith.users.infrastructure.repos.RoleRepository;
import com.dossantosh.springfirstmodulith.users.infrastructure.repos.SubmoduleRepository;

@Component
class JpaAccessCatalogImpl implements AccessCatalog {

    private final RoleRepository roleRepository;
    private final ModuleRepository moduleRepository;
    private final SubmoduleRepository submoduleRepository;

    JpaAccessCatalogImpl(RoleRepository roleRepository,
            ModuleRepository moduleRepository,
            SubmoduleRepository submoduleRepository) {
        this.roleRepository = roleRepository;
        this.moduleRepository = moduleRepository;
        this.submoduleRepository = submoduleRepository;
    }

    @Override
    public Optional<Roles> findRoleByName(String roleName) {
        return roleRepository.findByNameIgnoreCase(roleName)
                .map(AccessReferenceMapper::toDomain);
    }

    @Override
    public Optional<Modules> findModuleByName(String moduleName) {
        return moduleRepository.findByNameIgnoreCase(moduleName)
                .map(AccessReferenceMapper::toDomain);
    }

    @Override
    public Optional<Submodules> findSubmoduleByModuleAndName(String moduleName, String submoduleName) {
        return submoduleRepository.findByModuleNameAndSubmoduleName(moduleName, submoduleName)
                .map(AccessReferenceMapper::toDomain);
    }
}
