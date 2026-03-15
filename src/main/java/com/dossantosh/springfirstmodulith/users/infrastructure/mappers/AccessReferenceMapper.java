package com.dossantosh.springfirstmodulith.users.infrastructure.mappers;

import java.util.LinkedHashSet;
import java.util.Set;

import com.dossantosh.springfirstmodulith.users.domain.Modules;
import com.dossantosh.springfirstmodulith.users.domain.Roles;
import com.dossantosh.springfirstmodulith.users.domain.Submodules;
import com.dossantosh.springfirstmodulith.users.infrastructure.entities.ModuleJpaEntity;
import com.dossantosh.springfirstmodulith.users.infrastructure.entities.RoleJpaEntity;
import com.dossantosh.springfirstmodulith.users.infrastructure.entities.SubmoduleJpaEntity;

public final class AccessReferenceMapper {

    private AccessReferenceMapper() {
    }

    public static Roles toDomain(RoleJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        Roles role = new Roles();
        role.setId(entity.getId());
        role.setName(entity.getName());
        return role;
    }

    public static Modules toDomain(ModuleJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        Modules module = new Modules();
        module.setId(entity.getId());
        module.setName(entity.getName());
        return module;
    }

    public static Submodules toDomain(SubmoduleJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        Submodules submodule = new Submodules();
        submodule.setId(entity.getId());
        submodule.setName(entity.getName());
        submodule.setModule(toDomain(entity.getModule()));
        return submodule;
    }

    public static RoleJpaEntity toJpaEntity(Roles role) {
        if (role == null) {
            return null;
        }
        RoleJpaEntity entity = new RoleJpaEntity();
        entity.setId(role.getId());
        entity.setName(role.getName());
        return entity;
    }

    public static ModuleJpaEntity toJpaEntity(Modules module) {
        if (module == null) {
            return null;
        }
        ModuleJpaEntity entity = new ModuleJpaEntity();
        entity.setId(module.getId());
        entity.setName(module.getName());
        return entity;
    }

    public static SubmoduleJpaEntity toJpaEntity(Submodules submodule) {
        if (submodule == null) {
            return null;
        }
        SubmoduleJpaEntity entity = new SubmoduleJpaEntity();
        entity.setId(submodule.getId());
        entity.setName(submodule.getName());
        entity.setModule(toJpaEntity(submodule.getModule()));
        return entity;
    }

    public static Set<Roles> toDomainRoles(Set<RoleJpaEntity> entities) {
        LinkedHashSet<Roles> values = new LinkedHashSet<>();
        if (entities == null) {
            return values;
        }
        for (RoleJpaEntity entity : entities) {
            values.add(toDomain(entity));
        }
        return values;
    }

    public static Set<Modules> toDomainModules(Set<ModuleJpaEntity> entities) {
        LinkedHashSet<Modules> values = new LinkedHashSet<>();
        if (entities == null) {
            return values;
        }
        for (ModuleJpaEntity entity : entities) {
            values.add(toDomain(entity));
        }
        return values;
    }

    public static Set<Submodules> toDomainSubmodules(Set<SubmoduleJpaEntity> entities) {
        LinkedHashSet<Submodules> values = new LinkedHashSet<>();
        if (entities == null) {
            return values;
        }
        for (SubmoduleJpaEntity entity : entities) {
            values.add(toDomain(entity));
        }
        return values;
    }

    public static Set<RoleJpaEntity> toJpaRoleEntities(Set<Roles> roles) {
        LinkedHashSet<RoleJpaEntity> values = new LinkedHashSet<>();
        if (roles == null) {
            return values;
        }
        for (Roles role : roles) {
            values.add(toJpaEntity(role));
        }
        return values;
    }

    public static Set<ModuleJpaEntity> toJpaModuleEntities(Set<Modules> modules) {
        LinkedHashSet<ModuleJpaEntity> values = new LinkedHashSet<>();
        if (modules == null) {
            return values;
        }
        for (Modules module : modules) {
            values.add(toJpaEntity(module));
        }
        return values;
    }

    public static Set<SubmoduleJpaEntity> toJpaSubmoduleEntities(Set<Submodules> submodules) {
        LinkedHashSet<SubmoduleJpaEntity> values = new LinkedHashSet<>();
        if (submodules == null) {
            return values;
        }
        for (Submodules submodule : submodules) {
            values.add(toJpaEntity(submodule));
        }
        return values;
    }
}
